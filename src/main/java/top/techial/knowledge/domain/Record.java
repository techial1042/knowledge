package top.techial.knowledge.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author techial
 */
@Data
@Accessors(chain = true)
@Entity
@Table(indexes = {
        @Index(columnList = "nodeId")
})
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long nodeId;

    /**
     * operator
     */
    private String operator;

    private String message;

    private String content;

    @OneToOne
    private User user;

    /**
     * time
     */
    @CreationTimestamp
    private Instant createTime;

}