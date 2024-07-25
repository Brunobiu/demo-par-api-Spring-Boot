package com.mballem.demo_par_api.web.controller;

import com.mballem.demo_par_api.entity.Usuario;
import com.mballem.demo_par_api.service.UsuarioService;
import com.mballem.demo_par_api.web.dto.UsuarioCrateDto;
import com.mballem.demo_par_api.web.dto.UsuarioResponseDto;
import com.mballem.demo_par_api.web.dto.UsuarioSenhaDto;
import com.mballem.demo_par_api.web.dto.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.ErrorManager;

@Tag(name = "Usuarios", description = "Contém todas as as operações aos recursos para cadastro, edição e leitura de um usuário.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /* Adicionando usuario no banco - Metodo DTO */
    @Operation(summary = "Criar um novo usuário", description = "Recurso para criar um novo usuário",
    responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Usuário email ja cadastrado no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorManager.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processador por dados de entrada invalidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorManager.class)))
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> create(@Valid @RequestBody UsuarioCrateDto createDto) {
        Usuario user = usuarioService.salvar(UsuarioMapper.toUsuario(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(user));
    }

    /* Buscando usuario por id - Metodo DTO */
    @Operation(summary = "Recuperar um usuário por ID", description = "Recuperar um usuário por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso."
                    , content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorManager.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> getById(@PathVariable long id) {
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toDto(user));
    }

    /* Trocando a senha do usuario - Metodo DTO */
    @Operation(summary = "Atualizar senha", description = "Atualizar senha",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso."
                            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "Senha nao confere.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorManager.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorManager.class))),
                    @ApiResponse(responseCode = "422", description = "Campos invalidos ou mal formatados.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorManager.class)))
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable long id, @Valid @RequestBody UsuarioSenhaDto dto) {
        Usuario user = usuarioService.editarSenha(id, dto.getSenhaAtual(), dto.getNovaSenha(), dto.getConfirmaSenha());
        return ResponseEntity.noContent().build();
    }

    /* Buscando todos usuarios  - Metodo DTO */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> getAll() {
        List<Usuario> users = usuarioService.buscarTodos();
        return ResponseEntity.ok(UsuarioMapper.toListDto(users));
    }
}
