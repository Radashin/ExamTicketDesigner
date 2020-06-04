package pdf;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.printing.PDFPageable;

/**
 * Класс для работы с PDF-файлами
 *
 * @author Admin
 */
public class PDFfile {

    /**
     * Сохранение pdf-файла
     * @param textList список для сохранения
     * @param path путь сохранения
     * @throws IOException 
     */
    public static void writeList(List<String> textList, String path) throws IOException{
        //получение документа
        PDDocument document = createPDFfile(textList);
        try {
            //созранение документа
            document.save(checkPath(path));
        } finally {
            if (document != null) {
                //зактытие документа
                document.close();
            }
        }
    }

    /**
     * Печать pdf-файла
     * @param textList список для печать
     * @throws IOException
     * @throws PrinterException 
     */
    public static void print(List<String> textList) throws IOException, PrinterException {
        //получение документа
        PDDocument document = createPDFfile(textList);
        try {
            //получение объекта для рабоыт с принтером
            PrinterJob printJob = PrinterJob.getPrinterJob();
            //добавление объекта для печати
            printJob.setPageable(new PDFPageable(document));
            //открытие диалогового окна печати. Если нажата OK, то
            if (printJob.printDialog()) {
                //печатать страницу
                printJob.print();
            }
        } finally {
            if (document != null) {
                //закрыть документ
                document.close();
            }
        }       
    }

    /**
     * Создание pdf-документа
     * @param textList список для преобразования
     * @return объект типа PDDocument
     * @throws IOException 
     */
    private static PDDocument createPDFfile(List<String> textList) throws IOException {
        PDDocument document = null;
        //создание документа
        document = new PDDocument();
        //создание страницы
        PDPage page = new PDPage();
        //добавление страницы в документ
        document.addPage(page);
        //получение потока для работы со страницей
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        //загрузка из файла шрифта
        PDFont font = PDType0Font.load(document, new File("font/times.ttf"));
        //размер шрифта
        float fontSize = 20;
        //смещение строк
        float leading = 1.5f * fontSize;
        //получение прямоугольника страницы
        PDRectangle mediabox = page.getMediaBox();
        //отступ
        float margin = 30;
        //ширина страницы
        float width = mediabox.getWidth() - 2 * margin;
        //положение координаты X
        float startX = mediabox.getLowerLeftX() + margin;
        //положение координаты Y
        float startY = (float) (mediabox.getUpperRightY() - margin*1.5);
        //список строк по ширине страницы
        List<String> lines = new ArrayList<>();
        //обход всех исходных строк
        for (String text : textList) {
            //последний пробел
            int lastSpace = -1;
            //пока длина текста не равна 0...
            while (text.length() > 0) {
                //получение индекса пробельного символа
                int spaceIndex = text.indexOf(' ', lastSpace + 1);
                //если индекс меньше 0, то
                if (spaceIndex < 0) {
                    //пробельный индекс равен длине текста
                    spaceIndex = text.length();
                }
                //преобразование строки от 0 до пробельного индекса
                String subString = text.substring(0, spaceIndex);
                //вычисление длины строки
                float size = fontSize * font.getStringWidth(subString) / 1000;
                //если длина строки больше исходной ширинты, то
                if (size > width) {
                    //если последний пробельный индекс меньше 0, то
                    if (lastSpace < 0) {                       
                        lastSpace = spaceIndex;
                    }
                    //получение подстроки от 0 до последнего пробельного индекса
                    subString = text.substring(0, lastSpace);
                    //добавление в список строк
                    lines.add(subString);
                    //получение остальной часть строки
                    text = text.substring(lastSpace).trim();
                    lastSpace = -1;
                    //если пробельных индекс равен длине текста, то
                } else if (spaceIndex == text.length()) {
                    //добавить строку
                    lines.add(text);
                    //обнулить текст
                    text = "";
                } else {
                    //последний пробельный индекс равен пробельному индексу
                    lastSpace = spaceIndex;
                }
            }
        }
        //начало работы с потоком
        contentStream.beginText();
        //установка шрифта
        contentStream.setFont(font, fontSize);
        //установка позиции
        contentStream.newLineAtOffset(startX, startY);
        //установка текущего значения позиции
        float currentY = startY;
        //обход всех строк списка
        for (String line : lines) {
            //смещение текущей позиции
            currentY -= leading;
            //если текущая позиции меньше отступа, то
            if (currentY <= margin) {
                //завершить работу с тестом
                contentStream.endText();
                //закрыть
                contentStream.close();
                //создание новой страницы
                PDPage new_page = new PDPage();
                //добавление новой страницы
                document.addPage(new_page);
                //получение потока для новой страницы
                contentStream = new PDPageContentStream(document, new_page);
                //начало работы с потоком
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(startX, startY);
                //установка нового значение текущей позиции
                currentY = startY;
            }
            //запись текста
            contentStream.showText(line);
            //установка новой позиции
            contentStream.newLineAtOffset(0, -leading);
        }
        //зевершение работы с потоком
        contentStream.endText();
        //зактыртие потока
        contentStream.close();
        return document;
    }
    
    /**
     * Проверка корректности установки расширения в пути
     * @param path путь
     * @return корректный путь к файлу
     */
    private static String checkPath(String path){
        //если в конце пути содержится расширение .pdf, то
        if(path.substring(path.length()-4).toLowerCase().equals(".pdf")){
            //вернуть исходный путь
            return path;
        } else{
            //добавить в конец путь расширение .pdf
            return path + ".pdf";
        }
    }
}
