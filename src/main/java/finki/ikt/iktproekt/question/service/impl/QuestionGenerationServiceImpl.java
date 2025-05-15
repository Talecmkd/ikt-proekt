package finki.ikt.iktproekt.question.service.impl;

import finki.ikt.iktproekt.document.service.DocumentService;
import finki.ikt.iktproekt.exception.NotFoundEntityException;

import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.document.model.Document;

import finki.ikt.iktproekt.question.model.enumeration.QuestionType;
import finki.ikt.iktproekt.quiz.model.Quiz;

import finki.ikt.iktproekt.question.repository.QuestionRepository;


import finki.ikt.iktproekt.question.service.QuestionGenerationService;

import finki.ikt.iktproekt.quiz.repository.QuizRepository;

import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.azure.core.credential.AzureKeyCredential;

import com.azure.ai.inference.models.*;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionGenerationServiceImpl implements QuestionGenerationService {

    @Value("${azure.ai.endpoint}")
    private String endpoint;

    @Value("${azure.ai.api-key}")
    private String apiKey;

    @Value("${azure.ai.model}")
    private String modelName;

    private final QuestionRepository questionRepository;

    private final QuizRepository quizRepository;

    private final DocumentService documentService;

    @Override
    @Transactional
    public List<Question> generateQuestionsFromPdf(Long quizId, int numberOfQuestions) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundEntityException(Quiz.class));

        questionRepository.deleteAllByQuiz(quiz);

        Document document = documentService.findDocumentByQuiz(quiz);

        String text;
        try {
            text = extractTextFromPdf(document.getFilePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from pdf");
        }
        String prompt = buildPrompt(text, numberOfQuestions);
        String aiResponse = getAIResponse(prompt);
        return parseAndSaveQuestions(aiResponse, quiz);
    }

    private String extractTextFromPdf(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String buildPrompt(String text, int numberOfQuestions) {
        int trueFalseCount = Math.max(1, numberOfQuestions / 3);
        int multipleChoiceCount = numberOfQuestions - trueFalseCount;


        return String.format(
                "TEXT TO BASE QUESTIONS ON:\n%s\n\n" +
                        "GENERATE EXACTLY %d QUESTIONS:\n" +
                        "%d MULTIPLE CHOICE and %d TRUE/FALSE\n\n" +
                        "STRICT FORMATTING RULES - FOLLOW EXACTLY:\n\n" +
                        "MULTIPLE CHOICE QUESTIONS (%d):\n" +
                        "1. Start with: 'MC: [question]'\n" +
                        "2. Provide 4 options labeled A-D\n" +
                        "3. End with: 'Correct: [letter]'\n" +
                        "EXAMPLE:\n" +
                        "MC: What is the capital of France?\n" +
                        "A) London\nB) Paris\nC) Berlin\nD) Madrid\n" +
                        "Correct: B\n\n" +
                        "TRUE/FALSE QUESTIONS (%d):\n" +
                        "1. Start with: 'TF: [statement]'\n" +
                        "2. End with: 'Correct: [TRUE/FALSE]'\n" +
                        "EXAMPLE:\n" +
                        "TF: The capital of France is Paris.\n" +
                        "Correct: TRUE\n\n" +
                        "NON-NEGOTIABLE REQUIREMENTS:\n" +
                        "- Generate exactly %d MC and %d TF questions\n" +
                        "- Use EXACT formats above - no variations\n" +
                        "- Base ALL questions strictly on provided text\n" +
                        "- Mix question types in output (don't group)\n" +
                        "- Only output questions in specified format",
                text.substring(0, Math.min(text.length(), 5000)),
                numberOfQuestions,
                multipleChoiceCount,
                trueFalseCount,
                multipleChoiceCount,
                trueFalseCount,
                multipleChoiceCount,
                trueFalseCount
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

        return questions.isEmpty() ? Collections.emptyList() : questionRepository.saveAll(questions);
    }

    private Question parseSingleQuestion(String block, Quiz quiz) {
        if (block.startsWith("MC:")) {
            return parseMultipleChoiceQuestion(block, quiz);
        } else if (block.startsWith("TF:")) {
            return parseTrueFalseQuestion(block, quiz);
        }
        return null;
    }

    private Question parseMultipleChoiceQuestion(String block, Quiz quiz) {
        String[] lines = block.split("\n");
        if (lines.length < 3) return null;

        Question question = new Question();
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        question.setQuiz(quiz);
        question.setQuestionText(lines[0].replace("MC:", "").trim());

        List<String> answers = new ArrayList<>();
        for (int i = 1; i < lines.length - 1; i++) {
            if (lines[i].matches("^[A-D]\\) .*")) {
                answers.add(lines[i].substring(3).trim());
            }
        }

        if (answers.size() < 2) return null;
        question.setAnswers(answers);


        String correctLine = lines[lines.length - 1];
        if (correctLine.startsWith("Correct:")) {
            String correctLetter = correctLine.replace("Correct:", "").trim();
            if (!correctLetter.isEmpty()) {
                int index = correctLetter.charAt(0) - 'A';
                if (index >= 0 && index < answers.size()) {
                    question.setCorrectAnswer(answers.get(index));
                }
            }
        }

        return question.getCorrectAnswer() != null ? question : null;
    }

    private Question parseTrueFalseQuestion(String block, Quiz quiz) {
        String[] lines = block.split("\n");
        if (lines.length != 2) return null;

        Question question = new Question();
        question.setQuestionType(QuestionType.TRUE_FALSE);
        question.setQuiz(quiz);
        question.setQuestionText(lines[0].replace("TF:", "").trim());
        question.setAnswers(List.of("TRUE", "FALSE"));

        if (lines[1].startsWith("Correct:")) {
            String correct = lines[1].replace("Correct:", "").trim();
            if (correct.equalsIgnoreCase("TRUE") || correct.equalsIgnoreCase("FALSE")) {
                question.setCorrectAnswer(correct.toUpperCase());
            }
        }

        return question.getCorrectAnswer() != null ? question : null;
    }

}