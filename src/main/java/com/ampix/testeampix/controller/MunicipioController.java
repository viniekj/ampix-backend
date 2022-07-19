package com.ampix.testeampix.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ampix.testeampix.model.Municipio;
import com.ampix.testeampix.repository.MunicipioRepository;

@RestController
@RequestMapping("/municipios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MunicipioController {

	@Autowired
	private MunicipioRepository municipioRepository;

	@GetMapping
	public ResponseEntity<List<Municipio>> getAll() {
		return ResponseEntity.ok(municipioRepository.findAll());
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<Municipio> getById(@PathVariable Long id){
        return municipioRepository.findById(id)
            .map(resposta -> ResponseEntity.ok(resposta))
            .orElse(ResponseEntity.notFound().build());
    }

	@GetMapping("/codigo/{codigo}")
	public ResponseEntity<Municipio> getByCodigo(@PathVariable String codigo) {

		return ResponseEntity.ok(municipioRepository.findByCodigoIgnoreCase(codigo));
	}

	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Municipio>> getByNome(@PathVariable String nome) {
		return ResponseEntity.ok(municipioRepository.findByNomeContainingIgnoreCaseOrderByNome(nome));
	}

	@GetMapping("/uf/{uf}")
	public ResponseEntity<List<Municipio>> getByUf(@PathVariable String uf) {
		return ResponseEntity.ok(municipioRepository.findByUf(uf));
	}

	@PostMapping
	public ResponseEntity<Municipio> postMunicipio(@Valid @RequestBody Municipio municipio) {
		Municipio municipioDoBanco = municipioRepository.findByCodigoIgnoreCase(municipio.getCodigo());
		if (municipioDoBanco != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um código com este nome");
		}
		Municipio nomeDoBanco = municipioRepository.findByNomeAndUf(municipio.getNome(), municipio.getUf());
		if (nomeDoBanco != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe esse município");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(municipioRepository.save(municipio));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Municipio> putMunicipio(@PathVariable Long id, @Valid @RequestBody Municipio dados) {
		Optional<Municipio> municipio = municipioRepository.findById(id);
		if (municipio == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Município não existe");
		}
		Municipio municipioCodigoBanco = municipioRepository.findByCodigoIgnoreCase(dados.getCodigo());
		if (municipioCodigoBanco != null && municipioCodigoBanco.getId() != id) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código já existe em outro município.");
		}
		dados.setId(id);
		return ResponseEntity.status(HttpStatus.OK).body(municipioRepository.save(dados));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteMunicipio(@PathVariable Long id) {

		return municipioRepository.findById(id).map(resposta -> {
			municipioRepository.deleteById(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}).orElse(ResponseEntity.notFound().build());
	}

}
