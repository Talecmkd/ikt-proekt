package finki.ikt.iktproekt.Service.impl;


import finki.ikt.iktproekt.model.*;
import finki.ikt.iktproekt.model.enumeration.QuestionType;
import finki.ikt.iktproekt.repository.QuestionRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.ChatCompletions;

import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatRequestUserMessage;

import java.io.File;
import java.io.IOException;
import java.util.*;
@Service
public class QuestionGenerationServiceImpl {

    private final String endpoint;
    private final String apiKey;
    private final String modelName;
    private final QuestionRepository questionRepository;

    public QuestionGenerationServiceImpl(
            @Value("${azure.ai.endpoint}") String endpoint,
            @Value("${azure.ai.api-key}") String apiKey,
            @Value("${azure.ai.model}") String modelName,
            QuestionRepository questionRepository) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public List<Question> generateQuestionsFromPdf(String filePath, Quiz quiz) throws IOException {
        String text = extractTextFromPdf(filePath);
        
        String prompt = buildPrompt(text);
        
        String aiResponse = getAIResponse(prompt);
        System.out.println("AI Response: {}" + aiResponse);
        
        List<Question> questions = parseAndSaveQuestions(aiResponse, quiz);
        System.out.println("Generated {} questions " + questions.size());
        
        return questions;
    }

    private String extractTextFromPdf(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String buildPrompt(String text) {
        return String.format(
            "Extracted text:\n%s\n\nGenerate exactly 3 multiple-choice questions with:\n" +
            "- Format each question as:\n" +
            "Question: [question text]\n" +
            "A) [option1]\nB) [option2]\nC) [option3]\nD) [option4]\n" +
            "Answer: [correct letter]\n" +
            "- Ensure each question has exactly 4 options\n" +
            "- Provide only the formatted questions, no additional text",
            text.substring(0, Math.min(text.length(), 5000))
        );
    }

    private String getAIResponse(String prompt) {
        try {
            ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildClient();

            List<ChatRequestMessage> messages = List.of(
                new ChatRequestUserMessage(prompt)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
                .setModel(modelName)
                .setMaxTokens(1000)
                .setTemperature(0.7);

            ChatCompletions response = client.complete(options);
            return response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            System.out.println("Failed to get AI response " + e);
            throw new RuntimeException("AI service error", e);
        }
    }

    private List<Question> parseAndSaveQuestions(String response, Quiz quiz) {
        List<Question> questions = new ArrayList<>();
        String[] blocks = response.split("\n\n");
        
        for (String block : blocks) {
            try {
                Question question = parseSingleQuestion(block, quiz);
                if (question != null) {
                    questions.add(question);
                }
            } catch (Exception e) {
                System.err.println("Failed to parse question block: " + block);
                e.printStackTrace();
            }
        }
        
        if (!questions.isEmpty()) {
            return questionRepository.saveAll(questions);
        }
        return Collections.emptyList();
    }
    
    private Question parseSingleQuestion(String block, Quiz quiz) {
        if (!block.startsWith("Question:")) return null;
        
        String[] lines = block.split("\n");
        if (lines.length < 6) return null; 
        
        Question question = new Question();
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        question.setQuiz(quiz);
        question.setQuestionText(lines[0].replace("Question:", "").trim());
        
        List<String> answers = new ArrayList<>();
        for (int i = 1; i < lines.length && answers.size() < 4; i++) {
            if (lines[i].matches("^[A-D][).] .*")) { 
                answers.add(lines[i].substring(3).trim());
            }
        }
        
        if (answers.size() < 2) return null; 
        
        question.setAnswers(answers);
        
        
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].toLowerCase().startsWith("answer:")) {
                String correctLetter = lines[i].replaceAll("(?i)answer:", "").trim();
                if (!correctLetter.isEmpty()) {
                    int index = correctLetter.toUpperCase().charAt(0) - 'A';
                    if (index >= 0 && index < answers.size()) {
                        question.setCorrectAnswer(answers.get(index));
                        return question;
                    }
                }
                break;
            }
        }
        
        return null; 
    }
}