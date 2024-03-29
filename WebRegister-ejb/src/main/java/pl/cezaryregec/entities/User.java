package pl.cezaryregec.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SudoWaster <cezaryre@gmail.com>
 */
@Entity
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
    , @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id")
    , @NamedQuery(name = "User.findByMail", query = "SELECT u FROM User u WHERE u.mail = :mail")
    , @NamedQuery(name = "User.match", query = "SELECT u FROM User u WHERE u.id = :id AND u.mail = :mail")
    , @NamedQuery(name = "User.findByType", query = "SELECT u FROM User u WHERE u.type = :type")
    , @NamedQuery(name = "User.findByFirstname", query = "SELECT u FROM User u WHERE u.firstname = :firstname")
    , @NamedQuery(name = "User.findByLastname", query = "SELECT u FROM User u WHERE u.lastname = :lastname")})
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "mail")
    private String mail;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Column(name = "type")
    private int type;
    @Size(max = 64)
    @Column(name = "firstname")
    private String firstname;
    @Size(max = 64)
    @Column(name = "lastname")
    private String lastname;
    
    @OneToMany(mappedBy = "user_assignment")
    private List<GroupAssignment> groups = new ArrayList<>();
    
    @OneToMany(cascade = { 
        CascadeType.MERGE,
        CascadeType.PERSIST,
        CascadeType.REMOVE
    })
    @JoinColumn(name = "user_id")
    private List<Token> sessions = new ArrayList<>();
    
    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(name = "user_achievements",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id"))
    private List<Achievement> achievements = new ArrayList<>();
    
    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, String mail, String password, UserType type) {
        this.id = id;
        this.mail = mail;
        this.password = password;
        this.type = type.getInt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public UserType getType() {
        return UserType.cast(this.type);
    }

    public void setType(UserType type) {
        this.type = type.getInt();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public boolean hasPriviledge(UserType type) {
        return this.type >= type.getInt();
    }
    
    public List<Group> getGroups() {
        List<Group> result = new ArrayList<>();
        
        for(GroupAssignment assignment : groups) {
            result.add(assignment.getGroup());
        }
        
        return result;
    }
    
    @XmlTransient
    @JsonIgnore
    public List<GroupAssignment> getGroupAssignment() {
        return groups;
    }
    
    @XmlTransient
    @JsonIgnore
    public List<Token> getSessions() {
        return sessions;
    }
    
    public List<Achievement> getGroupAchievements(Integer groupId) {
        List<Achievement> result = new ArrayList<>();
        
        for(Achievement achievement : achievements) {
            if(achievement.getGroup().getId().equals(groupId)) {
                result.add(achievement);
            }
        }
        
        return result;
    }
    
    public Double getProgressInGroup(Integer groupId) {
        for(GroupAssignment group : groups) {
            if(group.getGroup().getId().equals(groupId)) {
                return group.getProgress();
            }
        }
        
        throw new NotFoundException("Not in group " + groupId);
    }
    
    
    public void giveAchievement(Achievement achievement) {
        achievements.add(achievement);
        achievement.getUsers().add(this);
    }
    
    public void denyAchievement(Achievement achievement) {
        achievements.remove(achievement);
        achievement.getUsers().remove(this);
    }
    
    public Double getAttendanceInGroup(Integer groupId) {
        for(GroupAssignment group : groups) {
            if(group.getGroup().getId().equals(groupId)) {
                return group.getAttendance();
            }
        }
        
        throw new NotFoundException("Not in group " + groupId);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        
        return this.mail.equals(other.mail)
                && ((this.firstname == null && other.firstname == null) || this.firstname.equals(other.firstname))
                && ((this.lastname == null && other.lastname == null) || this.lastname.equals(other.lastname))
                && this.type == other.type;
    }

    @Override
    public String toString() {
        return "pl.cezaryregec.entities.User[ id=" + id + ", mail=" + mail + " ]";
    }
    
}
