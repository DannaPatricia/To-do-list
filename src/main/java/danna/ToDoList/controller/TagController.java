package danna.ToDoList.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// DTOs
import danna.ToDoList.dto.TagDto.CreateTagDto;
import danna.ToDoList.dto.TagDto.TagByUserDto;
import danna.ToDoList.dto.TagDto.UpdateTagDto;
// Servicios y seguridad
import danna.ToDoList.security.CustomUserDetails;
import danna.ToDoList.service.TagService;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // Obtener la etiquetas del usaurio
    @GetMapping("/me")
    public ResponseEntity<List<TagByUserDto>> tagsByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return tagService.tagsByUser(customUserDetails.getId());
    }

    // Crear una nueva etiqueta
    @PostMapping("/create")
    public ResponseEntity<String> postMethodName(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateTagDto createTagDto) {
        if (!createTagDto.isValid()) {
            return ResponseEntity.status(409).body("Datos invalidos");
        }
        return tagService.createTag(createTagDto, customUserDetails.getId());
    }

    // Canbiar el nombre de una etiqueta
    @PutMapping("/update")
    public ResponseEntity<String> putMethodName(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UpdateTagDto updateTagDto) {
        if (!updateTagDto.isValid()) {
            return ResponseEntity.status(409).body("Datos invalidos");
        }
        return tagService.updateTag(customUserDetails.getId(), updateTagDto);
    }

    // Eliminar una etiqueta
    @DeleteMapping("/delete/{tagId}")
    public ResponseEntity<String> deleteTag(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long tagId) {
        return tagService.deleteTag(customUserDetails.getId(), tagId);
    }
}