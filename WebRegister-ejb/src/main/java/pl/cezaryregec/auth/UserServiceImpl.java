package pl.cezaryregec.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import java.io.IOException;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import pl.cezaryregec.Config;
import pl.cezaryregec.entities.Token;
import pl.cezaryregec.entities.User;
import pl.cezaryregec.entities.UserType;

/**
 *
 * @author SudoWaster <cezaryre@gmail.com>
 */
@Stateless
public class UserServiceImpl implements UserService {
    
    //@PersistenceContext(unitName="pl.cezaryregec_WebRegister-ejb_ejb_1.0-SNAPSHOTPU")
    @Inject
    private Provider<EntityManager> entityManager;
    
    @Inject
    private HashGenerator hashGenerator;
    
    @Inject
    private ObjectMapper objectMapper;
    
    @Inject
    private Config config;
    
    

    @Override
    @Transactional
    public void createUser(String mail, String password, String firstname, String lastname, UserType type) {
        String hashedPassword = getHashedPassword(mail, password);
        
        if(userExists(mail)) {
            throw new ForbiddenException();
        }
        
        User user = new User();
        user.setMail(mail);
        user.setPassword(hashedPassword);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setType(type);
        
        entityManager.get().merge(user);
    }
    
    @Override
    @Transactional
    public void setUser(String updatedUserJson, String password, String tokenId) throws IOException {
        
        User updatedUser = objectMapper.readValue(updatedUserJson, User.class);
        User currentUser = getUser(getToken(tokenId).getUserId());
        
        String hashedPassword = getHashedPassword(currentUser.getMail(), password);
        
        
        if(currentUser.getType() != UserType.ADMIN
                && !Objects.equals(currentUser.getId(), updatedUser.getId())) {
            
            throw new ForbiddenException();
        }
        
        if(!currentUser.checkPassword(hashedPassword)) {
            throw new ForbiddenException();
        }
        
        entityManager.get().merge(updatedUser);
    }
    
    @Override
    @Transactional
    public String getUserJson(String mail) throws NoResultException, JsonProcessingException {
        
        User user = getUser(mail);
        
        return objectMapper.writeValueAsString(user);
    }
    
    @Override
    @Transactional
    public String getUserJson(int id) throws NoResultException, JsonProcessingException {
        
        User user = getUser(id);
        
        return objectMapper.writeValueAsString(user);
    }
    
    @Override
    @Transactional
    public String getRegisteredTokenJson(String userJson, String password, String fingerprint) 
            throws NotAuthorizedException, IOException {
        
        User actualUser = matchUser(userJson);
        
        String hashedPassword = getHashedPassword(actualUser.getMail(), password);
                
        if(actualUser.checkPassword(hashedPassword)) {
            return objectMapper.writeValueAsString(createToken(actualUser, fingerprint));
        }
        
        throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED));
    }
    
    private User getUser(String mail) throws NoResultException {
        
        Query userQuery = entityManager.get().createNamedQuery("User.findByMail");
        userQuery.setParameter("mail", mail);
        
        return (User) userQuery.getSingleResult();
    }
    
    private User getUser(int id) throws NoResultException {
            
        Query userQuery = entityManager.get().createNamedQuery("User.findById");
        userQuery.setParameter("id", id);
        
        return (User) userQuery.getSingleResult();
    }
    
    private User matchUser(String userJson) throws IOException {
        User passedUser = objectMapper.readValue(userJson, User.class);
        
        Query userQuery = entityManager.get().createNamedQuery("User.match");
        userQuery.setParameter("id", passedUser.getId());
        userQuery.setParameter("mail", passedUser.getMail());
        
        return (User) userQuery.getSingleResult();
    }
    
    private boolean userExists(String mail) {
        
        try {
            User existing = getUser(mail);
            
            return existing != null;
            
        } catch(NoResultException ex) {
            
            return false;
        }
    }
    
    @Override
    @Transactional
    public void removeToken(String tokenId) {
        
        Token token = getToken(tokenId);
        
        entityManager.get().remove(token);
    }

    @Override
    @Transactional
    public boolean isTokenValid(String tokenId, String fingerprint) {
        Token token = getToken(tokenId);
        boolean isValid = token.isValid(fingerprint);
        
        if(!token.hasExpired()) {
            removeToken(tokenId);
        }
        
        return isValid;
    }
    
    private Token createToken(User user, String fingerprint) {
        
        Token token = new Token();
        token.setExpiration(config.getSessionTime());
        token.setUser(user.getId());
        token.setFingerprint(fingerprint);

        entityManager.get().merge(token);
        
        return token;
    }
    
    private Token getToken(String tokenId) {
        Query tokenQuery = entityManager.get().createNamedQuery("Token.findById", Token.class);
        tokenQuery.setParameter("id", tokenId);
        
        return (Token) tokenQuery.getSingleResult();
    }

    @Override
    public String getFingerprint(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String remoteAddress = request.getHeader("HTTP_X_FORWARDED_FOR");

        if (remoteAddress == null) {
            remoteAddress = request.getRemoteAddr();
        }
        
        return userAgent + "\n" + remoteAddress;
    }
    
    private String getHashedPassword(String mail, String password) {
        return hashGenerator.getSaltHash(getFormatedForHash(mail, password), config.getSaltPhrase());
    }
    
    private String getFormatedForHash(String mail, String password) {
        return mail + password;
    }
}