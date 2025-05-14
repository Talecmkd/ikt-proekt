package finki.ikt.iktproekt.pdfexport.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.question.service.QuestionService;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {
    private final QuizService quizService;
    private final QuestionService questionService;

    public byte[] exportQuizToPdf(Long quizId) throws DocumentException {
        Quiz quiz = quizService.findById(quizId);
        java.util.List<Question> questions = questionService.findAllByQuizId(quizId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph(quiz.getTitle(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        Font questionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font answerFont = new Font(Font.FontFamily.HELVETICA, 10);

        int questionNumber = 1;
        for (Question question : questions) {
            Paragraph qText = new Paragraph(questionNumber + ". " + question.getQuestionText(), questionFont);
            qText.setSpacingAfter(10f);
            document.add(qText);

            List<String> answers = question.getAnswers();
            for (int i = 0; i < answers.size(); i++) {
                char option = (char) ('A' + i);
                Paragraph answer = new Paragraph("   " + option + ") " + answers.get(i), answerFont);
                document.add(answer);
            }

            document.add(new Paragraph(" "));
            questionNumber++;
        }

        document.close();
        return baos.toByteArray();
    }
}