package pl.cezaryregec.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SudoWaster <cezaryre@gmail.com>
 */
@Entity
@Table(name = "presence")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Presence.findAll", query = "SELECT p FROM Presence p"),
    @NamedQuery(name = "Presence.findGroup", query = "SELECT p FROM Presence p WHERE p.group.id = :id"),
    @NamedQuery(name = "Presence.findInGroupByDate", query = "SELECT p FROM Presence p WHERE p.group.id = :id AND p.date = :date"),
    @NamedQuery(name = "Presence.findByUser", query = "SELECT p FROM Presence p WHERE p.user.id = :id"),
    @NamedQuery(name = "Presence.findByUserInGroup", query = "SELECT p FROM Presence p WHERE p.user.id = :uid AND p.group.id = :gid"),
    @NamedQuery(name = "Presence.findByUserInGroupByDate", query = "SELECT p FROM Presence p WHERE p.user.id = :uid AND p.group.id = :gid AND p.date = :date")
})
public class Presence implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
//    @Basic(optional = false)
//    @Column(name = "group_id")
//    private Integer groupId;
    @Basic(optional = false)
    @Column(name = "date")
    private Date date;
    @Basic(optional = false)
    @Column(name = "presence")
    private Boolean presence;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;
    
    public Presence() {
        
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
    
    @XmlTransient
    @JsonIgnore
    public Group getGroup() {
        return group;
    }
    
    public void setGroup(Group group) {
        this.group = group;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public Boolean getPresence() {
        return presence;
    }
    
    public void setPresence(Boolean presence) {
        this.presence = presence;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Presence)) {
            return false;
        }
        Presence other = (Presence) object;
        return (this.user.equals(other.user) && this.group.equals(other.group) && this.date.equals(other.date)) 
                || ((this.id == null && other.id == null) || this.id.equals(other.id)) ;
    }

    @Override
    public String toString() {
        return "pl.cezaryregec.entities.Presence[ id=" + id + ", user_id = " + user.getId() + ", group_id = " + group.getId() + ", date = " + date + ", presence = " + presence + " ]";
    }
}
