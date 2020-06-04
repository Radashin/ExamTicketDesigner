package controller;

import data.Question;
import data.Ticket;
import gui.Alarm;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pdf.PDFfile;

/**
 * Класс-контроллер
 *
 * @author Admin
 */
public class DesignerController implements Initializable {

    @FXML
    private AnchorPane anchorPaneBase;
    @FXML
    private MenuItem menuItemLoad;
    @FXML
    private MenuItem menuItemClearAndLoad;
    @FXML
    private MenuItem menuItemSaveTXT;
    @FXML
    private MenuItem menuItemSavePDF;
    @FXML
    private MenuItem menuItemPrint;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private MenuItem menuItemExit;
    @FXML
    private ListView<String> listQuestion;
    @FXML
    private Label labelQuantityQuestion;
    @FXML
    private TableView<Ticket> tableTicket;
    @FXML
    private TableColumn<Ticket, String> columnTicketName;
    @FXML
    private TableColumn<Ticket, Integer> columnTicketQuantity;
    @FXML
    private Label labelQuantityTicket;
    @FXML
    private TableView<Question> tableQuestionByTicket;
    @FXML
    private TableColumn<Question, Integer> columnQuestionByTicketNumber;
    @FXML
    private TableColumn<Question, String> columntQuestionByTicketQuestion;
    @FXML
    private TextField textFieldQuestion;
    @FXML
    private CheckBox checkBoxMyQuestion;
    @FXML
    private Button buttonDeleteQuestion;
    @FXML
    private Button buttonAddQuestion;
    @FXML
    private Button buttonAddTicket;
    @FXML
    private Button buttonDeleteTicket;
    @FXML
    private Button buttonDeleteQuestionInList;
    @FXML
    private ComboBox<Integer> comboBoxQuantityQuestionByTicket;

    private ObservableList<Ticket> listTicket;
    private ObservableList<Question> listQuestionByTicket;
    private ObservableList<Integer> listComboBoxQuantityQuestionByTicket;
    private ObservableList<String> listListQuestion;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //кнопка "Добавить вопрос" неактивна пока не выбран добавляемый вопрос и пока не выбран билет и пока не выбрано число вопросов на 1 билет
        buttonAddQuestion.disableProperty().bind(listQuestion.getSelectionModel().selectedItemProperty().isNull()
                .or(tableTicket.getSelectionModel().selectedItemProperty().isNull()).or(comboBoxQuantityQuestionByTicket.valueProperty().isNull()));
        //конопка "Удалить билет" будет неактивна пока не выбран вопрос в таблице вопросов билета
        buttonDeleteQuestion.disableProperty().bind(tableQuestionByTicket.getSelectionModel().selectedItemProperty().isNull());
        //кнопка "Добавить билет" неактивна пока не выбрано число вопросов на 1 билет
        buttonAddTicket.disableProperty().bind(comboBoxQuantityQuestionByTicket.valueProperty().isNull());
        //кнопка "Удалить билет" будет неактивна пока не выбран билет в таблице билетов
        buttonDeleteTicket.disableProperty().bind(tableTicket.getSelectionModel().selectedItemProperty().isNull());
        //кнопка "Удалить" будет неактивна пока не выбран вопрос в списке вопросов
        buttonDeleteQuestionInList.disableProperty().bind(listQuestion.getSelectionModel().selectedItemProperty().isNull());

        initComboBoxQuantityQuestionByTicket();

        initListQuestion();

        initTableTicket();
        initTableQuestionByTicket();

        setTextInLabelQuantityQuestion();
        setTextInLabelQuantityTicket();

        //установка слушателя выбора строки в таблице с билетами
        tableTicket.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ticket>() {
            @Override
            public void changed(ObservableValue<? extends Ticket> observable, Ticket oldValue, Ticket newValue) {
                if (newValue != null) {
                    listQuestionByTicket.clear();
                    listQuestionByTicket.addAll(newValue.getListQuestionByTicket());
                } else {
                    listQuestionByTicket.clear();
                }
            }
        });

        //установка слушателя выбора четкобка добавления вопроса
        checkBoxMyQuestion.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                //если чекбокс выбран, то
                if (newValue) {
                    //кнопка "Добавить вопрос" будет неактивна пока текстовое поле с вопросом не будет содержить текста
                    buttonAddQuestion.disableProperty().bind(textFieldQuestion.textProperty().isEmpty()
                            .or(textFieldQuestion.textProperty().isNull()
                                    .or(tableTicket.getSelectionModel().selectedItemProperty().isNull()
                                            .or(comboBoxQuantityQuestionByTicket.valueProperty().isNull()))));
                } else {
                    //кнопка "Добавить вопрос" неактивна пока не выбран добавляемый вопрос и пока не выбран билет и пока не выбрано число вопросов на 1 билет
                    buttonAddQuestion.disableProperty().bind(listQuestion.getSelectionModel().selectedItemProperty().isNull()
                            .or(tableTicket.getSelectionModel().selectedItemProperty().isNull()).or(comboBoxQuantityQuestionByTicket.valueProperty().isNull()));
                    textFieldQuestion.clear();
                }
            }
        });
    }

    /**
     * Инициализация таблицы со списком билетов
     */
    private void initTableTicket() {
        //установка значений для колонок таблицы
        columnTicketName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnTicketQuantity.setCellValueFactory(new PropertyValueFactory<>("quantityQuestion"));
        //инициализация коллекции
        listTicket = FXCollections.observableArrayList();
        //добавление коллекции в таблицу
        tableTicket.setItems(listTicket);
    }

    /**
     * Инициализация таблицы со списком вопросов для выбранного билета
     */
    private void initTableQuestionByTicket() {
        //установка значений для колонок таблицы
        columnQuestionByTicketNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        columntQuestionByTicketQuestion.setCellValueFactory(new PropertyValueFactory<>("name"));
        //инициализация коллекции
        listQuestionByTicket = FXCollections.observableArrayList();
        //добавление коллекции в таблицу
        tableQuestionByTicket.setItems(listQuestionByTicket);
    }

    /**
     * Инициализация списка загруженных вопросов
     */
    private void initListQuestion() {
        listListQuestion = FXCollections.observableArrayList();
        listQuestion.setItems(listListQuestion);
    }

    /**
     * Инициализация комбобокса с количеством вопросов для каждого билета
     */
    private void initComboBoxQuantityQuestionByTicket() {
        //инициализация коллекции
        listComboBoxQuantityQuestionByTicket = FXCollections.observableArrayList();
        //добавление элементов в коллекцию
        for (int i = 1; i < 21; i++) {
            listComboBoxQuantityQuestionByTicket.add(i);
        }
        //добавление коллекции в комбобокс
        comboBoxQuantityQuestionByTicket.setItems(listComboBoxQuantityQuestionByTicket);
    }

    /**
     * Установка значения количества доступных вопросов
     */
    private void setTextInLabelQuantityQuestion() {
        labelQuantityQuestion.setText("Количество доступных вопросов: " + listListQuestion.size());
    }

    /**
     * Установка значения количества созданных билетов
     */
    private void setTextInLabelQuantityTicket() {
        labelQuantityTicket.setText("Количество созданных билетов: " + listTicket.size());
    }

    /**
     * Метод-слушатель выбора меню "Очистить и загрузить"
     */
    @FXML
    private void menuItemClearAndLoadOnAction() {
        load(true);
    }

    /**
     * Метод-слушатель выбора меню "Загрузить"
     */
    @FXML
    private void menuItemLoadOnAction() {
        load(false);
    }

    /**
     * Загрузка вопросов
     *
     * @param clear очищать список вопросов
     */
    private void load(boolean clear) {
        //Диалоговое окно
        FileChooser fc = new FileChooser();
        fc.setTitle("Окно выбора файла");
        //Путь к файлу
        File file = fc.showOpenDialog((Stage) anchorPaneBase.getScene().getWindow());
        if (file != null) {
            String path = file.getAbsolutePath();
            List<String> readAllLines = new ArrayList<>();
            try {
                //чтение файла
                readAllLines = Files.readAllLines(Paths.get(path), Charset.forName("CP1251"));

            } catch (IOException ex) {
                try {
                    //чтение файла
                    readAllLines = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
                } catch (IOException ex1) {
                    Alarm.showAlert(Alert.AlertType.ERROR, "Загрузка", "Ошибка загрузки", "Объект не был загружен.");
                    Logger.getLogger(DesignerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (clear) {
                //очистка листа списка
                listListQuestion.clear();
            }
            //добавление новых вопросов
            listListQuestion.addAll(readAllLines);
            //обновление списка
            listQuestion.refresh();
        }
        setTextInLabelQuantityQuestion();
    }

    /**
     * Метод-слушатель выбора меню "Сохранить в txt"
     */
    @FXML
    private void menuItemSaveTXTOnAction() {
        //проверка корректности составления билетов
        boolean checkToFile = checkToFile();
        if (!checkToFile) {
            return;
        }

        //Создание объекты типа FileChooser
        FileChooser fc = new FileChooser();
        //Установка названия объекту
        fc.setTitle("Окно сохранения файла");
        //Отображение окна для уазания путь сохранения файла
        File file = fc.showSaveDialog((Stage) anchorPaneBase.getScene().getWindow());
        if (file != null) {
            //Получение абсолютного пути
            String path = file.getAbsolutePath();
            try {
                if(!path.substring(path.length()-4).toLowerCase().equals(".pdf")){
                    path = path + ".txt";
                }  
                //запись в файл
                Files.write(Paths.get(path), toFile());
                //диалоговое окно с подтверждением успешной записи в файл
                Alarm.showAlert(Alert.AlertType.INFORMATION, "Сохранение", "Cохранения выполнено", "Путь к файлу: " + path);
            } catch (IOException ex) {
                //диалоговое окно с сообщением об ошибке записи в файл
                Alarm.showAlert(Alert.AlertType.ERROR, "Сохранение", "Ошибка сохранения", "Объект не был сохранен.");
                Logger.getLogger(DesignerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Метод-слушатель выбора меню "Сохранить в pdf"
     */
    @FXML
    private void menuItemSavePDFOnAction() {
        //проверка корректности составления билетов
        boolean checkToFile = checkToFile();
        if (!checkToFile) {
            return;
        }

        //Создание объекты типа FileChooser
        FileChooser fc = new FileChooser();
        //Установка названия объекту
        fc.setTitle("Окно сохранения файла");
        //Отображение окна для уазания путь сохранения файла
        File file = fc.showSaveDialog((Stage) anchorPaneBase.getScene().getWindow());
        if (file != null) {
            //Получение абсолютного пути
            String path = file.getAbsolutePath();
            try {
                //запись в файл
                PDFfile.writeList(toFile(), path);
                if(!path.substring(path.length()-4).toLowerCase().equals(".pdf")){
                    path = path + ".pdf";
                }       
                //диалоговое окно с подтверждением успешной записи в файл
                Alarm.showAlert(Alert.AlertType.INFORMATION, "Сохранение", "Cохранения выполнено", "Путь к файлу: " + path);
            } catch (IOException ex) {
                //диалоговое окно с сообщением об ошибке записи в файл
                Alarm.showAlert(Alert.AlertType.ERROR, "Сохранение", "Ошибка сохранения", "Объект не был сохранен.");
                Logger.getLogger(DesignerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Получение списка для записи в файл
     *
     * @return список с данными
     */
    private List<String> toFile() {
        List<String> listToFile = new ArrayList<>();
        //обход всех билетов
        for (Ticket t : listTicket) {
            //получение названия билета
            String name = t.getName();
            //получение списка всех вопросов билета
            List<Question> listQuestionByTicket1 = t.getListQuestionByTicket();
            //добавдение названия
            listToFile.add("     " + name);
            //добавление всех вопросов
            for (Question q : listQuestionByTicket1) {
                listToFile.add(q.toString());
            }
        }
        return listToFile;
    }

    /**
     * Получение разрешение на запись в файл. Проводится проверка на
     * соответствие корректности составления билетов
     *
     * @return true - если все составлено корректно, иначе - false
     */
    private boolean checkToFile() {
        //получение значения числа вопросов в билете
        Integer maxQuestion = comboBoxQuantityQuestionByTicket.getSelectionModel().getSelectedItem();
        if (maxQuestion == null) {
            //отображение диалогового окна
            Alarm.showAlert(Alert.AlertType.INFORMATION, "Составление билетов", "Число вопросов в билете", "Не задано число вопросов в билете!");
            return false;
        }
        boolean ok = true;
        //проверка на равенство вопросов в билете максимальному числу
        for (Ticket tic : listTicket) {
            if (tic.getQuantityQuestion() != maxQuestion) {
                ok = false;
                break;
            }
        }
        //если число вопросов не соответствует заданному или список пуст, то
        if (!ok || listTicket.isEmpty()) {
            String msg;
            //формаривание сообщения
            if (listTicket.isEmpty()) {
                msg = "Список билетов пуст!!!";
            } else {
                msg = "Число вопросов в некоторых или во всех билетах не соответствует заданному значению!";
            }
            //отображение диалогового окна
            Alarm.showAlert(Alert.AlertType.INFORMATION, "Составление билетов", "Не до конца составлены билеты", msg);
            return false;
        }
        return true;
    }

    /**
     * Метод-слушатель выбора меню "Печать"
     */
    @FXML
    private void menuItemPrintOnAction() {

        //проверка корректности составления билетов
        boolean checkToFile = checkToFile();
        if (!checkToFile) {
            return;
        }

        try {
            PDFfile.print(toFile());
        } catch (IOException ex) {
            Logger.getLogger(DesignerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PrinterException ex) {
            Logger.getLogger(DesignerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Метод-слушатель выбора меню "Выход"
     */
    @FXML
    private void menuItemExitOnAction() {
        Stage stage = (Stage) anchorPaneBase.getScene().getWindow();
        stage.close();
    }

    /**
     * Метод-слушатель выбора меню "О программе"
     */
    @FXML
    private void menuItemAboutOnAction() {
        Alarm.showAlert(Alert.AlertType.INFORMATION, "О программе", "Конструктор экзаменационных билетов",
                "Данная программа разработана в целях быстрого и удобного создания экзаменационных билетов.");
    }

    /**
     * Метод-слушатель нажатия на кнопку "Добавить билет"
     */
    @FXML
    private void buttonAddTicketOnAction() {
        //создание билета
        Ticket t = new Ticket();
        //добавление билета
        listTicket.add(t);
        //обновление кол-ва созданных билетов
        setTextInLabelQuantityTicket();
    }

    /**
     * Метод-слушатель нажатия на кнопку "Удалить билет"
     */
    @FXML
    private void buttonDeleteTicketOnAction() {
        //получение удаляемого индекса из таблицы с билетами
        int selectedIndex = tableTicket.getSelectionModel().getSelectedIndex();
        //удаление билета из списка
        Ticket remove = listTicket.remove(selectedIndex);
        //если билет удален, то
        if (remove != null) {
            //уменьшение счетчика билетов
            Ticket.count = Ticket.count - 1;
            //переименование всех билетов начиная с удаляемого индекса
            for (int i = selectedIndex; i < listTicket.size(); i++) {
                //получение билета
                Ticket ticket = listTicket.get(i);
                //установка нового имени
                ticket.setName("Билет№ " + (selectedIndex + 1));
                //увеличение индекса на 1
                selectedIndex++;
            }
            //обновление таблицы с билетами
            tableTicket.refresh();
            //обновление кол-ва созданных билетов
            setTextInLabelQuantityTicket();
            //получение всех вопросов из удаляемого билета
            List<Question> list = remove.getListQuestionByTicket();
            //добавление всех вопросов в список загруженных вопросов (возврат назад в список)
            for (Question q : list) {
                listListQuestion.add(q.getName());
            }
        }
    }

    /**
     * Метод-слушатель нажатия на кнопку "Добавить вопрос"
     */
    @FXML
    private void buttonAddQuestionOnAction() {
        //получение значения максимального числа вопросов для билета
        Integer maxQuestions = comboBoxQuantityQuestionByTicket.getSelectionModel().getSelectedItem();
        //получение индекса выделенной строки в таблице с билетами
        int selectedIndexTicket = tableTicket.getSelectionModel().getSelectedIndex();
        //получение билета
        Ticket ticket = listTicket.get(selectedIndexTicket);
        //если число вопросов в билета больше или равно максально заданному, то
        if (ticket.getQuantityQuestion() >= maxQuestions) {
            //отобразить диалоговое окно с предупреждением и выйти из метода
            Alarm.showAlert(Alert.AlertType.WARNING, "Добавление вопроса", "Достигнуто максимальное количество",
                    String.format("Заданное количество вопросов для билета: %d. Данный билет содержит %d вопроса(ов)", maxQuestions, ticket.getQuantityQuestion()));
            return;
        }
        //если выбран чекбокс, то
        if (checkBoxMyQuestion.isSelected()) {
            //получение текста из текстового поля
            String text = textFieldQuestion.getText();
            //добавление вопроса в билет
            ticket.addQuestion(text);
            //обновление таблицы
            tableTicket.refresh();
            //очистка поля
            textFieldQuestion.clear();
        } else {
            //получение индекса выделенного вопроса
            int selectedIndexQuestion = listQuestion.getSelectionModel().getSelectedIndex();
            //удаление вопроса из списка вопросов
            String question = listListQuestion.remove(selectedIndexQuestion);
            //добавление вопроса в билет
            ticket.addQuestion(question);
            //обновление таблицы
            tableTicket.refresh();
        }

        tableTicket.getSelectionModel().clearSelection();
        tableTicket.getSelectionModel().select(selectedIndexTicket);

    }

    /**
     * Метод-слушатель нажатия на кнопку "Удалить вопрос"
     */
    @FXML
    private void buttonDeleteQuestionOnAction() {
        //получение индекса удаляемого вопроса
        int selectedIndexTicket = tableTicket.getSelectionModel().getSelectedIndex();
        //получение индекса билета, из которого удаляется вопрос
        int selectedIndexQuestion = tableQuestionByTicket.getSelectionModel().getSelectedIndex();
        //получение билета
        Ticket ticket = listTicket.get(selectedIndexTicket);
        //удаление вопроса
        String removeQuestion = ticket.removeQuestion(selectedIndexQuestion);
        //обновление таблицы
        tableTicket.refresh();
        //повторное выделение строки в таблице с билетами
        tableTicket.getSelectionModel().clearSelection();
        tableTicket.getSelectionModel().select(selectedIndexTicket);
        //добавление вопроса назад в список вопросов
        listListQuestion.add(removeQuestion);
        //обновить строку с информацией о количестве созданных билетов
        setTextInLabelQuantityTicket();
    }

    /**
     * Метод-слушатель нажатия на кнопку "Удалить"
     */
    @FXML
    private void buttonDeleteQuestionInListOnAction() {
        //получение индекса из списка
        int selectedIndex = listQuestion.getSelectionModel().getSelectedIndex();
        //удаление вопроса из списка
        listListQuestion.remove(selectedIndex);
    }
}
