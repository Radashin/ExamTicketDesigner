package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс - билет
 *
 * @author Admin
 */
public class Ticket {

    public static int count = 0;
    private String name;
    private int quantityQuestion;
    private List<String> listQuestion;

    public Ticket() {
        count = count + 1;
        this.name = "Билет№ " + count;
        quantityQuestion = 0;
        listQuestion = new ArrayList<>();
    }

    /**
     * Получение названия билета
     *
     * @return название
     */
    public String getName() {
        return name;
    }

    /**
     * Установка названия билета
     *
     * @param name название
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получение количества вопросов в билете
     *
     * @return число вопросов
     */
    public int getQuantityQuestion() {
        return quantityQuestion;
    }

    /**
     * Получение списка вопросов
     *
     * @return список вопросов
     */
    public List<String> getListQuestion() {
        return listQuestion;
    }

    /**
     * Добавление вопроса в билет
     *
     * @param question вопрос
     * @return true - в случае добавления, иначе - false
     */
    public boolean addQuestion(String question) {
        boolean add = listQuestion.add(question);
        if (add) {
            quantityQuestion = listQuestion.size();
        }
        return add;
    }

    /**
     * Удаление вопроса по индексу
     * @param idx индекс
     * @return вопрос, если индекс выходит за допустимые пределы, то - null
     */
    public String removeQuestion(int idx) {
        //проверка корректности индекса
        if(idx < 0 || idx >= quantityQuestion){
            return null;
        }
        //удаление
        String remove = listQuestion.remove(idx);
        if (remove != null) {
            quantityQuestion = listQuestion.size();
        }
        return remove;
    }

    /**
     * Удаление вопроса по вопросу
     * @param question вопрос
     * @return true - в случае удаление, иначе - false
     */
    public boolean removeQuestion(String question) {
        //удаление
        boolean remove = listQuestion.remove(question);
        if (remove) {
            quantityQuestion = listQuestion.size();
        }
        return remove;
    }
    
    /**
     * Получение списка вопросов с порядковыми номерами
     * @return список вопросов типа Question
     */
    public List<Question> getListQuestionByTicket(){
        List<Question> list = new ArrayList<>();
        int count = 0;
        for(String question : listQuestion){
            list.add(new Question(++count, question));
        }
        return list;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + this.quantityQuestion;
        hash = 43 * hash + Objects.hashCode(this.listQuestion);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        if (this.quantityQuestion != other.quantityQuestion) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.listQuestion, other.listQuestion)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + ", кол-во вопросов = " + quantityQuestion;
    }

}
