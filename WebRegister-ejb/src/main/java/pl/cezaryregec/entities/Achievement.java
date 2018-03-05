package pl.cezaryregec.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SudoWaster <cezaryre@gmail.com>
 */
@Entity
@Table(name = "groups")
@XmlRootElement
public class Achievement implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "achievement_name")
    private String name;
    @Basic(optional = false)
    @Size(min = 1, max = 256)
    @Column(name = "achievement_desc")
    private String description;
    
    @OneToOne(mappedBy = "achievements")
    private Group group;
    
    public Achievement() {
        
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setGroup(Group group) {
        this.group = group;
    }
    
    public Group getGroup() {
        return group;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Achievement)) {
            return false;
        }
        Achievement other = (Achievement) object;
        return this.id.equals(other.id) || (this.name.equals(other.name) && this.description.equals(other.description));
    }

    @Override
    public String toString() {
        return "pl.cezaryregec.entities.Achievement[ id=" + id + ", name = " + name + ", description = " + description + ", group = " + group.getId() + " ]";
    }
}
