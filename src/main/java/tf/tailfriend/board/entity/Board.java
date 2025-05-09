package tf.tailfriend.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import tf.tailfriend.file.entity.File;
import tf.tailfriend.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boards")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type_id", nullable = false)
    private BoardType boardType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardPhoto> photos = new ArrayList<>();

    public void addPhoto(File file) {
        BoardPhoto photo = BoardPhoto.builder()
                .board(this)
                .file(file)
                .build();
        photos.add(photo);
    }

    public void removePhoto(File file) {
        photos.removeIf(photo -> photo.getFile().equals(file));
    }
}
