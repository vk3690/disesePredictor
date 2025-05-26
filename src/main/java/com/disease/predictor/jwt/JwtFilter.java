//package com.disease.predictor.jwt;
//
//import com.disease.predictor.entity.Users;
//import com.disease.predictor.repo.UserDetailsRepo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JWTService jwtService;
//
//
//
//    @Autowired
//    UserDetailsRepo userDetailsRepo;
//    private String username;
//
//
//    private static final Logger logger = LoggerFactory.getLogger("jwtService");
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        try {
//
//                logger.info("{}",request.getRequestURL());
//                String authToken = request.getHeader("Authorization");
//                if(this.checkForInternalRequest(request.getRequestURI()))
//                {
//                    filterChain.doFilter(request, response);
//
//                }else if (authToken != null && authToken.startsWith("Basic ")) {
//                    authToken = authToken.replace("Basic ", "");
//                    try {
//                        if (this.validateUser(authToken, response, request)) {
//                            filterChain.doFilter(request, response);
//                        } else {
//                            response.sendError(401, "UNAUTHORISED");
//                        }
//                    } catch (IllegalArgumentException e) {
//                        logger.error("Error occurred while retrieving Username from Token", e);
//                        response.sendError(401, "UNAUTHORISED");
//                    } catch (Exception e) {
//                        logger.error("Authentication Failed. Invalid username or password.");
//                        response.sendError(401, "UNAUTHORISED");
//                    }
//
//
//            }
//        } catch (Exception e) {
//            logger.warn("Error to validate request ");
//            response.sendError(401, "UNAUTHORISED");
//
//        }
//        filterChain.doFilter(request,response);
//    }
//
//    private boolean validateUser(String authToken, HttpServletResponse response, HttpServletRequest request) {
//        byte[] decodedBytes = Base64.getDecoder().decode(authToken);
//        String username=new String(decodedBytes);
//        Users users=userDetailsRepo.findByUsername(username.split(":")[0]);
//        if(users!=null) {
//            return true;
//        }else{
//            return false;
//        }
//    }
//
//
//    private boolean checkForInternalRequest(String requestURI) {
//
//        List<String> patterns = new ArrayList<>();
//
//        patterns.add("/h2-console");
//        patterns.add("/injectData");
//
//        boolean match = false;
//        for (String strpattern : patterns) {
//            if (requestURI.contains(strpattern)) {
//                match = true;
//                break;
//            } else {
//                continue;
//            }
//        }
//        return match;
//    }
//}