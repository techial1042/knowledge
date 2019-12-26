package top.techial.knowledge.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author techial
 */
@Data
@Entity
@Accessors(chain = true)
public class Storage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SHA-1
     */
    @Id
    private String id;

    private String originalFilename;

    private String contentType;

    @CreationTimestamp
    private Instant createTime;

    @UpdateTimestamp
    private Instant updateTime;

}
