package com.example.lab7_20211755.Controllers;

import com.example.lab7_20211755.Entities.Resource;
import com.example.lab7_20211755.Entities.User;
import com.example.lab7_20211755.Repositories.ResourceRepository;
import com.example.lab7_20211755.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/niupay")
public class WebServiceController {

    final UserRepository userRepository;
    final ResourceRepository resourceRepository;

    public WebServiceController(UserRepository userRepository, ResourceRepository resourceRepository) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    //Listas auxiliares:

    ArrayList<Integer> listaRecursos = new ArrayList<>(List.of(5, 6, 7, 8));
    ArrayList<String> listaTipo = new ArrayList<>(List.of("Contador", "Cliente", "AnalistaPromociones", "AnalistaLogistico"));

    // Métodos:

    //Default
    @GetMapping(value = {"/",""})
    public Object inicio(){
        return  "Bienvenido a NIUPAY!";
    }

    @PostMapping(value = {"/contador"})
    public ResponseEntity<HashMap<String, Object>> crearContador(@RequestParam(value = "name",required = false) String name) {
        HashMap<String, Object> respuesta = new HashMap<>();

        if(name == null){
            respuesta.put("Mensaje","Debe incluir el nombre de usuario.");
            return ResponseEntity.badRequest().body(respuesta);
        }

        if(!name.isEmpty() && name.length()<=100){
            User user = new User();
            user.setName(name);
            user.setType("Contador");
            user.setAuthorizedResource(5);
            user.setActive(true);
            userRepository.save(user);
            respuesta.put("Estado","OK");
            respuesta.put("Mensaje","Usuario "+name+" contador creado exitosamente. (ID de usuario = "+user.getUserId()+")");
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        }else{
            respuesta.put("Estado","ERROR");
            respuesta.put("Mensaje","El nombre de usuario debe contener como mínimo 1 caracter y como máximo 100 caracteres.");
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @PostMapping(value = {"/acceso"})
    public ResponseEntity<HashMap<String, Object>> crearContador(@RequestParam(value = "userId", required = false) String userIdStr,
                                                                 @RequestParam(value = "authorizedResource", required = false) String authorizedResourceStr) {
        HashMap<String, Object> respuesta = new HashMap<>();
        User user = new User();
        Resource resource = new Resource();

        if(userIdStr == null || authorizedResourceStr== null){
            respuesta.put("Mensaje","Debe incluir el id del usuario y el id del recurso.");
            return ResponseEntity.badRequest().body(respuesta);
        }

        HashMap<String,Integer> listaTipoRecurso = new HashMap<String,Integer>();
        listaTipoRecurso.put("Contador",5);
        listaTipoRecurso.put("Cliente",6);
        listaTipoRecurso.put("AnalistaPromociones",7);
        listaTipoRecurso.put("AnalistaLogistico",5);

        try {
            int userId = Integer.parseInt(userIdStr);
            int authorizedResource = Integer.parseInt(authorizedResourceStr);

            Optional<User> userOpt = userRepository.findById(userId);

            //Validaciones:
            boolean validacion = true;

            if(userOpt.isPresent()){
                user = userOpt.get();
                if(!user.isActive()){
                    respuesta.put("MensajeA","El usuario no puede autenticarse.");
                    validacion = false;
                }
            }else{
                respuesta.put("MensajeA","El id usuario no es válido.");
                validacion = false;
            }

            if(listaRecursos.contains(authorizedResource)){
                resource = resourceRepository.findById(authorizedResource).get();
            }else{
                respuesta.put("MensajeB","El id de recurso no es válido.");
                validacion = false;
            }

            if(validacion){
                if(listaTipoRecurso.get(user.getType()) == authorizedResource){
                    respuesta.put("Mensaje","Bienvenido Usuario "+user.getName()+". Puede acceder al recurso: "+resource.getName()+".");
                    return ResponseEntity.ok(respuesta);
                }else{
                    respuesta.put("MensajeC","El usuario "+user.getName()+" no puede acceder al recurso '"+resource.getName()+"'.");
                    return ResponseEntity.badRequest().body(respuesta);
                }
            }else{
                return ResponseEntity.badRequest().body(respuesta);
            }

        }catch (NumberFormatException e){
            respuesta.put("Mensaje","El id del usuario y el id del recurso deben ser número enteros positivos.");
            return ResponseEntity.badRequest().body(respuesta);
        }

    }

    @GetMapping(value = {"/listado/{authorizedResource}"})
    public ResponseEntity<HashMap<String, List<Object>>> listaUsuarios(@PathVariable(value = "authorizedResource",required = false) String authorizedResourceStr){

        HashMap<String, List<Object>> respuesta = new HashMap<>();

        HashMap<Integer,String> listaTipoRecurso = new HashMap<Integer,String>();
        listaTipoRecurso.put(5,"Contador");
        listaTipoRecurso.put(6,"Cliente");
        listaTipoRecurso.put(7,"AnalistaPromociones");
        listaTipoRecurso.put(8,"AnalistaLogistico");

        if(authorizedResourceStr == null){
            respuesta.put("Mensaje", Collections.singletonList("Debe incluir el id del usuario y el id del recurso."));
            return ResponseEntity.badRequest().body(respuesta);
        }

        try {

            int authorizedResource = Integer.parseInt(authorizedResourceStr);

            if(listaRecursos.contains(authorizedResource)){
                Resource resource = resourceRepository.findById(authorizedResource).get();

                respuesta.put("Recurso", Collections.singletonList(resource.getName()));
                respuesta.put("Usuarios con acceso",userRepository.findAllByTypeEquals(listaTipoRecurso.get(authorizedResource)));
                return ResponseEntity.ok(respuesta);
            }else{
                respuesta.put("Mensaje", Collections.singletonList("El recurso ingresado no existe."));
                return ResponseEntity.badRequest().body(respuesta);
            }

        }catch (NumberFormatException e){
            respuesta.put("Mensaje", Collections.singletonList("El id del recurso debe ser número un número entero positivo."));
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<HashMap<String, Object>> borrar(@RequestParam(value = "userId", required = false) String userIdStr){
        HashMap<String, Object> respuesta = new HashMap<>();

        if(userIdStr == null){
            respuesta.put("Mensaje", Collections.singletonList("Debe incluir el id del usuario."));
            return ResponseEntity.badRequest().body(respuesta);
        }

        try{
            int id = Integer.parseInt(userIdStr);
            Optional<User> byId = userRepository.findById(id);

            if(byId.isPresent()){
                userRepository.deleteById(id);
                respuesta.put("Mensaje","El jugador fue eliminado exitosamente.");
            }else{
                respuesta.put("Mensaje","El ID enviado no existe.");
                return ResponseEntity.badRequest().body(respuesta);
            }

            return ResponseEntity.ok(respuesta);
        }catch (NumberFormatException e){
            respuesta.put("Mensaje","El id un número entero positivo.");
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    //Actualizar tipo
    @PutMapping(value = {"/actualizar"})
    public ResponseEntity<HashMap<String, Object>> actualizar(@RequestParam(value = "userId", required = false) String userIdStr,
                                                                @RequestParam(value = "type", required = false) String type) {

        HashMap<String, Object> respuesta = new HashMap<>();
        User user = new User();

        HashMap<String,Integer> listaTipoRecurso = new HashMap<String,Integer>();
        listaTipoRecurso.put("Contador",5);
        listaTipoRecurso.put("Cliente",6);
        listaTipoRecurso.put("AnalistaPromociones",7);
        listaTipoRecurso.put("AnalistaLogistico",5);

        if(userIdStr == null){
            respuesta.put("Mensaje","Debe incluir el id del usuario a eliminar y el tipo a cambiar.");
            return ResponseEntity.badRequest().body(respuesta);
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            int authorizedResourceNew = 0;

            Optional<User> userOpt = userRepository.findById(userId);

            //Validaciones:
            boolean validacion = true;

            if(userOpt.isPresent()){
                user = userOpt.get();
            }else{
                respuesta.put("MensajeA","El id usuario no es válido.");
                validacion = false;
            }

            if(!listaTipo.contains(type)){
                respuesta.put("MensajeB","El tipo no es válido.");
                validacion = false;
            }

            if(validacion){
                authorizedResourceNew = listaTipoRecurso.get(type);
                userRepository.cambiarTipoPorId(userId,type,authorizedResourceNew);
                respuesta.put("Mensaje","Se cambió el tipo del usuario "+user.getName()+" a "+type+" exitosamente.");
                return ResponseEntity.ok(respuesta);
            }else{
                return ResponseEntity.badRequest().body(respuesta);
            }

        }catch (NumberFormatException e){
            respuesta.put("Mensaje","El id del usuario deben ser un número entero positivo.");
            return ResponseEntity.badRequest().body(respuesta);
        }

    }

    @PostMapping(value = {"/apagar"})
    public ResponseEntity<HashMap<String, Object>> apagar() {
        HashMap<String, Object> respuesta = new HashMap<>();

        userRepository.apagar();

        respuesta.put("Mensaje","Todos los usuarios fueron apagados exitosamente.");
        return ResponseEntity.ok(respuesta);
    }
}
