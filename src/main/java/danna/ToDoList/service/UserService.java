package danna.ToDoList.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import danna.ToDoList.dto.UserDto;
import danna.ToDoList.dto.UserRegisterDto;
import danna.ToDoList.model.UserEntity;
import danna.ToDoList.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // Inyeccion por constructor de dependencias, en lugar de crar una instancia
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        
    }

    // Response entity es un objeto que representa toda la respuesta http
    // DTO -> data transfer object -> para separar la estructura de datos del cliente, 
    // evitando exponer la lógica interna o la estructura completa de las entidades de la base de datos.
    public ResponseEntity<UserRegisterDto> createUser(UserRegisterDto newUserDto){
        // Se crea un userEntity a partir del userDto
        UserEntity userEntity = new UserEntity(newUserDto);
        if(!userRepository.existsByEmail(userEntity.getEmail()) && !userRepository.existsByUsername(userEntity.getUsername())){
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

            UserEntity userSaved = userRepository.save(userEntity);
            return ResponseEntity.ok(new UserRegisterDto(userSaved));
            // Response entity devuelve un status y un body
            // status -> ok(body) -> 200
            // body -> userRepository.save(newUser)
            // devuelve codigo 200, ok

            // Otra manera de hacerlo
            // return ResponseEntity.ok(new UserRequestDto(userRepository.save(userEntity)));
        }
        // Caso en que el usuario ya existe, devuelve conflicto
        // con el .build() se devuelve la respuesta sin cuerpo
        return ResponseEntity.status(409).build();
    }

    // Login de usuario
    // public ResponseEntity<UserDto> loginUser(UserDto userDto){
    //     return userRepository.findByUsername(userDto.getUsername())
    //     // Se filtra por el usuario cuya contraseña coincida
    //     .filter(user -> passwordEncoder.matches(userDto.getPassword(), user.getPassword()))
    //     // se obtiene el mismo user y se transforma la entidad en dto como respuesta
    //     .map(user -> ResponseEntity.ok(new UserDto(user)))
    //     // si no existe el user o la contraseña no coincide se manda un body vacio
    //     .orElse(ResponseEntity.status(401).build());
    // }

    
    public ResponseEntity<UserDto> loginUser(UserDto userDto, HttpServletRequest request){
        // Spring valida automaticamente el usuario usando el CustomUserDetailsService
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
        );

        // Se guarda la sesion + la cookie JSESSIONID a nivel java, no se crea en el servidor?
        SecurityContextHolder.getContext().setAuthentication(auth);
        // fuerza creacion de la sesion HTTP y cookie JSESSIONID
        request.getSession(true);
        // Se obtiene el user entity para devolver el dto por seguridad, en caso de no encontrar la entidad lanza una excepcion
        UserEntity userEntity = userRepository.findByUsername(userDto.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Se devuelve el dto
        return ResponseEntity.ok(new UserDto(userEntity));
    }

    // Metodo que encuentra coincidencias con el string pasado desde el frontend
    // DIFERENCIA ENTRE LIST Y STREAM
    // LIST -> Es una colección de objetos que se almacena directamente en la memoria principal del programa.
    // STREAM -> es una secuencia de procesamiento que opera sobre los datos (que pueden provenir de una lista) de manera eficiente y perezosa, mejorando el rendimiento
    public List<UserDto> searchUserByUsername(String usernamePart){
        return userRepository.findByUsernameContainingIgnoreCase(usernamePart)
        // Convieto la lista en un stream de java para poder acceder a sus funciones
        .stream()
        // trasnforma cada elemento del stream enun dto
        .map(user -> new UserDto(user)) // o lo que es lo mismo (UserLoginDto::new)
        // se transforma el stream en una lsita
        .toList();
    }
}
