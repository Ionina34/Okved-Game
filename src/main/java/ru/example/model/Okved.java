package ru.example.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель одного узла классификатора ОКВЭД (раздел, класс, подкласс, группа, вид деятельности)
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Okved {

    /**
     * Код, может быть "Раздел А","01", "01.1", "01.11" и.т.д)
     */
    private String code;

    /**
     * Полное наименование вида деятельности / раздела
     */
    private String name;

    /**
     * Вложенные элементы (null или пустой список, если это листовой узел - конкретный вид деятельности
     */
    @SerializedName("items")
    private List<Okved> children;

    @Override
    public String toString() {
        return "Okved{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", childrenCount='" + (children != null ? children.size() : 0) + '\'' +
                '}';
    }
}
