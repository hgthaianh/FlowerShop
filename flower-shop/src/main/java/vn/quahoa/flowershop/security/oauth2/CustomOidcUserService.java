package vn.quahoa.flowershop.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import vn.quahoa.flowershop.model.AuthProvider;
import vn.quahoa.flowershop.model.User;
import vn.quahoa.flowershop.repository.UserRepository;
import vn.quahoa.flowershop.security.UserPrincipal;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        // reuse logic from CustomOAuth2UserService, or duplicate it here for simplicity
        // For OIDC, email is usually available in attributes
        String email = oidcUser.getEmail();
        if (email == null) {
            // fallback to attributes if getEmail() is null (though OidcUser usually has it)
            email = (String) oidcUser.getAttributes().get("email");
        }

        if (email == null) {
            throw new InternalAuthenticationServiceException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.GOOGLE)) {
                throw new InternalAuthenticationServiceException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oidcUser);
        } else {
            user = registerNewUser(userRequest, oidcUser);
        }

        return UserPrincipal.create(user, oidcUser.getAttributes());
    }

    private User registerNewUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        User user = new User();

        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(oidcUser.getSubject());
        user.setName(oidcUser.getFullName());
        if (user.getName() == null) {
            user.setName((String) oidcUser.getAttributes().get("name"));
        }
        user.setEmail(oidcUser.getEmail());
        user.setImageUrl(oidcUser.getPicture());
        user.setEmailVerified(true); // OIDC implies email verified mostly
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OidcUser oidcUser) {
        existingUser.setName(oidcUser.getFullName());
        if (existingUser.getName() == null) {
            existingUser.setName((String) oidcUser.getAttributes().get("name"));
        }
        existingUser.setImageUrl(oidcUser.getPicture());
        return userRepository.save(existingUser);
    }
}
