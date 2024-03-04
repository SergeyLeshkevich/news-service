package ru.clevertec.news.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime time;

    @FullTextField(analyzer = "english")
    @Column(nullable = false, length = 50)
    private String title;

    @FullTextField(analyzer = "english")
    @Column(nullable = false, length = 2000)
    private String text;

    @OneToOne
    @IndexedEmbedded
    @JoinColumn(name = "user_id")
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private User user;

    @Column(name = "is_archive",nullable = false)
    private boolean isArchived;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return isArchived == news.isArchived
                && Objects.equals(id, news.id)
                && Objects.equals(time, news.time)
                && Objects.equals(title, news.title)
                && Objects.equals(text, news.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, title, text, isArchived);
    }
}
