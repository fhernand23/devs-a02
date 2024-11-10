package frontend.communication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import model.to.code.transformation.ModelToCodeMapper;
import model.to.image.transformation.ModelToImageTransformationService;
import text.to.model.transformation.TextToModelTransformationService;

@RestController
public class MensajeController {

    @PostMapping("/mensaje/post")
    public ResponseEntity<?> recibirMensaje(@RequestBody Mensaje mensaje) {

        try {
            Boolean exito = TextToModelTransformationService.semanticValidationService(mensaje.getContenido());

            if (exito) {
                ModelToCodeMapper mapper = new ModelToCodeMapper();
                mapper.setFile(new File(TextToModelTransformationService.getArchivo_modelo().toFileString())); // Configura el archivo modelo
                mapper.traduccion_acceleo(mapper.getFile());

                File zipFile = mapper.getZipFile(); 
                Path path = zipFile.toPath();
                byte[] content = Files.readAllBytes(path);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", zipFile.getName());

                return new ResponseEntity<>(content, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TextToModelTransformationService.getSemanticErrors());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el archivo: " + e.getMessage());
        }
    }
    
    @PostMapping("/mensaje/crear-imagen")
    public ResponseEntity<?> crearImagenDesdeTexto(@RequestBody Mensaje mensaje) {
        try {
            Boolean exito = TextToModelTransformationService.semanticValidationService(mensaje.getContenido());

            if (exito) {
                byte[] imageBytes = Files.readAllBytes(ModelToImageTransformationService.getImage().toPath());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                headers.setContentLength(imageBytes.length);

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TextToModelTransformationService.getSemanticErrors());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return (ResponseEntity<byte[]>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

