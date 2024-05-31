package com.example.lab7_20211755.Controllers;

import com.example.lab7_20211755.Entities.Resource;
import com.example.lab7_20211755.Entities.User;
import com.example.lab7_20211755.Repositories.ResourceRepository;
import com.example.lab7_20211755.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<HashMap<String, Object>> crearContador(@RequestParam(value = "userId") String userIdStr,
                                                                 @RequestParam(value = "authorizedResource") String authorizedResourceStr) {
        HashMap<String, Object> respuesta = new HashMap<>();
        User user = new User();
        Resource resource = new Resource();

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
    public ResponseEntity<HashMap<String, List<Object>>> listaUsuarios(@PathVariable("authorizedResource") String authorizedResourceStr){

        HashMap<String, List<Object>> respuesta = new HashMap<>();

        try {

            int authorizedResource = Integer.parseInt(authorizedResourceStr);

            if(listaRecursos.contains(authorizedResource)){



            }else{
                respuesta.put("Mensaje", Collections.singletonList("El recurso ingresado no existe."));
                return ResponseEntity.badRequest().body(respuesta);
            }

        }catch (NumberFormatException e){
            respuesta.put("Mensaje", Collections.singletonList("El id del recurso deben ser número un número enteros positivo."));
            return ResponseEntity.badRequest().body(respuesta);
        }
    }
}
