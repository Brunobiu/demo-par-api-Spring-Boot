package com.mballem.demo_par_api.service;

import com.mballem.demo_par_api.entity.Usuario;
import com.mballem.demo_par_api.exception.EntityNotFoundExceptio;
import com.mballem.demo_par_api.exception.UsernameUniqueViolationException;
import com.mballem.demo_par_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /* Adicionando usuario no banco */
    @Transactional
    public Usuario salvar(Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepository.save(usuario);
        }catch (org.springframework.dao.DataIntegrityViolationException ex ) {
            throw new UsernameUniqueViolationException(String.format("E-mail '%s' já cadastrado !!!", usuario.getUsername()));
        }
    }

    /* Buscando usuario por id */
    @Transactional(readOnly = true)
    public Usuario buscarPorId(long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundExceptio(String.format("Usuário com id=%s não encontrado!", id))
        );
    }

    /* Trocando a senha do usuario */
    @Transactional
    public Usuario editarSenha(long id, String password) {
        Usuario user = buscarPorId(id);
        user.setPassword(password);
        return user;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario editarSenha(long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if (!novaSenha.equals(confirmaSenha)){
            throw  new RuntimeException("Nova senha não confere!");
        }

        Usuario user = buscarPorId(id);
        if (!passwordEncoder.matches(senhaAtual, user.getPassword())) {
            throw  new RuntimeException("Sua senha não confere.!");
        }

        user.setPassword(passwordEncoder.encode(novaSenha));
        return user;

    }


    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundExceptio(String.format("Usuário com '%s' não encontrado!", username))
        );
    }

    @Transactional(readOnly = true)
    public Usuario.Role buscarRolePorUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }
}
