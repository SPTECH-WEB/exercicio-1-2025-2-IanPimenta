package school.sptech.prova_ac1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuarios = repository.findAll();

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (repository.findByCpf(usuario.getCpf()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario novoUsuario = repository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return repository.findById(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam LocalDate nascimento) {
        List<Usuario> usuarios = repository.findByDataNascimentoAfter(nascimento);

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        Optional<Usuario> existente = repository.findById(id);

        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario emailEmUso = repository.findByEmail(usuario.getEmail());
        if (emailEmUso != null && !emailEmUso.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario cpfEmUso = repository.findByCpf(usuario.getCpf());
        if (cpfEmUso != null && !cpfEmUso.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario usuarioAtualizado = existente.get();
        usuarioAtualizado.setNome(usuario.getNome());
        usuarioAtualizado.setEmail(usuario.getEmail());
        usuarioAtualizado.setCpf(usuario.getCpf());
        usuarioAtualizado.setSenha(usuario.getSenha());
        usuarioAtualizado.setDataNascimento(usuario.getDataNascimento());

        repository.save(usuarioAtualizado);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}