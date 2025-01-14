package com.editorial.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@Table(name = "article_draft")
public class ArticleDraft implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_draft")
    private Long id;

    @Column(name = "title")
    @Size(min = 3, max = 200, message = "Title must contain more than 2 and less than 201 characters!")
    @Pattern(regexp = "^[^<>*%:&/\\\\]+[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\s]+[0-9]*$", message = "Title must not contain such characters as:<>*%:&/\\")
    private String title;

    @Column(name = "content")
    @NotBlank(message = "Content must not be blank!")
    private String content;

    @Column(name = "date_of_completion")
    private Timestamp dateOfCompletion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journalist_id_d")
    private User journalist;

    @Override
    public int hashCode() {
        return Objects.hash(title, content, dateOfCompletion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        ArticleDraft other = (ArticleDraft) o;
        return Objects.equals(title, other.title)
                && Objects.equals(content, other.content)
                && Objects.equals(dateOfCompletion, other.dateOfCompletion);
    }
}
