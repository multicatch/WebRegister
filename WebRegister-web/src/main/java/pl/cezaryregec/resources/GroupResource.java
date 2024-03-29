package pl.cezaryregec.resources;

import com.google.inject.servlet.RequestScoped;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.cezaryregec.auth.TokenService;
import pl.cezaryregec.groups.GroupService;
import pl.cezaryregec.entities.GroupRole;
import pl.cezaryregec.entities.User;
import pl.cezaryregec.entities.UserType;

/**
 *
 * @author SudoWaster <cezaryre@gmail.com>
 */
@RequestScoped
@Path("groups")
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
public class GroupResource {
    
    private final TokenService tokenService;
    private final GroupService groupService;
    
    @Inject
    public GroupResource(TokenService tokenService, GroupService groupService) {
        this.tokenService = tokenService;
        this.groupService = groupService;
    }
    
    @GET
    public Response getOpenGroups(@QueryParam("token") String token,
            @Context HttpServletRequest request) {
        
        tokenService.validateToken(token, request);
        
        return Response.ok(groupService.getOpenGroups()).build();
    }
    
    @GET
    @Path("all")
    public Response getAllGroups(@QueryParam("token") String token,
            @Context HttpServletRequest request) {
        
        tokenService.validateToken(token, request);
        
        if(!tokenService.getToken(token).getUser().hasPriviledge(UserType.PRIVILEDGED)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.ok(groupService.getGroups()).build();
    }
    
    @POST
    public Response createGroup(@FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("vacancies") Integer vacancies,
            @QueryParam("token") String token,
            @Context HttpServletRequest request) {
        
        tokenService.validateToken(token, request);
        
        if(!tokenService.getToken(token).getUser().hasPriviledge(UserType.PRIVILEDGED)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        User user = tokenService.getToken(token).getUser();
        
        Integer groupId = groupService.createGroup(name, description, vacancies);
        groupService.addToGroup(user, groupId, false);
        groupService.setRole(user, groupId, GroupRole.PRIVILEDGED);
        
        return Response.status(Response.Status.CREATED).build();
    }
    
    @PUT
    public Response setGroup(@FormParam("id") Integer id,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("vacancies") Integer vacancies,
            @QueryParam("token") String token,
            @Context HttpServletRequest request) {

        tokenService.validateToken(token, request);
        
        if(!groupService.isPriviledgedInGroup(tokenService.getToken(token).getUser(), id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        groupService.setGroup(id, name, description, vacancies);
        
        return Response.status(Response.Status.OK).build();
    }
    
    @DELETE
    public Response deleteGroup(@FormParam("id") Integer id,
            @QueryParam("token") String token,
            @Context HttpServletRequest request) {
        
        tokenService.validateToken(token, request);
        
        if(!groupService.isPriviledgedInGroup(tokenService.getToken(token).getUser(), id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        groupService.deleteGroup(id);
        
        return Response.status(Response.Status.OK).build();
    }
}
