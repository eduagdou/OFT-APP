package com.example.oftbackend.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insforge")
@CrossOrigin(origins = "*")
public class InsforgeProxyController {

    private final RestTemplate restTemplate;

    @Value("${insforge.base-url:}")
    private String baseUrl;

    @Value("${insforge.api-key:}")
    private String apiKey;

    public InsforgeProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    void validateConfig() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("INSFORGE_BASE_URL debe estar configurada");
        }
        baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    private HttpHeaders headers(boolean preferRepresentation) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El servidor no tiene configurada la clave de InsForge"
            );
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        if (preferRepresentation) {
            headers.set("Prefer", "return=representation");
        }
        return headers;
    }

    private String recordsUrl(String table) {
        return baseUrl + "api/database/records/" + table;
    }

    private Map<String, Object> auth(String path, Map<String, Object> body) {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "api/auth/" + path + "?client_type=mobile",
                HttpMethod.POST,
                new HttpEntity<>(body, headers(false)),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() == null ? Map.of() : response.getBody();
    }

    private List<Map<String, Object>> getRecords(String table) {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                recordsUrl(table) + "?limit=1000",
                HttpMethod.GET,
                new HttpEntity<>(headers(false)),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() == null ? List.of() : response.getBody();
    }

    private Map<String, Object> createRecord(String table, Map<String, Object> record) {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                recordsUrl(table),
                HttpMethod.POST,
                new HttpEntity<>(List.of(record), headers(true)),
                new ParameterizedTypeReference<>() {}
        );
        List<Map<String, Object>> rows = response.getBody();
        return rows == null || rows.isEmpty() ? record : rows.get(0);
    }

    private Map<String, Object> mapPaciente(Map<String, Object> row) {
        return new LinkedHashMap<>(row);
    }

    private Map<String, Object> mapUsuario(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>(row);
        Object rolId = row.get("rol_id");
        Object sucursalId = row.get("sucursal_id");
        result.put("rol", rolId == null ? "PACIENTE" : "ROL_" + rolId);
        result.put("sucursal", sucursalId == null ? "" : "SUCURSAL_" + sucursalId);
        result.put("fotoPerfilUri", row.get("foto_perfil_uri"));
        return result;
    }

    private Map<String, Object> mapAtencion(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>(row);
        Object sucursalId = row.get("sucursal_id");
        result.put("pacienteNombre", "Paciente " + row.get("paciente_id"));
        result.put("medicoNombre", row.get("medico_id") == null ? null : "Usuario " + row.get("medico_id"));
        result.put("sucursal", sucursalId == null ? "" : "Sucursal " + sucursalId);
        return result;
    }

    private Map<String, Object> mapExamen(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>(row);
        result.put("pacienteId", row.get("paciente_id"));
        result.put("pacienteNombre", "Paciente " + row.get("paciente_id"));
        result.put("medicoId", row.get("medico_id"));
        result.put("medicoNombre", row.get("medico_id") == null ? null : "Usuario " + row.get("medico_id"));
        result.put("tipoExamen", row.get("tipo_examen"));
        result.put("ojoEvaluado", row.get("ojo_evaluado"));
        result.put("fechaExamen", row.get("fecha_examen"));
        result.put("fechaResolucion", row.get("fecha_resolucion"));
        result.put("estadoEntrega", row.get("estado_entrega"));
        result.put("comentarios", row.get("descripcion"));
        result.put("archivosAdjuntos", null);
        result.put("sincronizado", true);
        return result;
    }

    private Map<String, Object> mapDocumento(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>(row);
        result.put("examenId", row.get("examen_id"));
        result.put("nombreArchivo", row.get("nombre_archivo"));
        result.put("tipoArchivo", row.get("tipo_archivo"));
        result.put("tamanio", row.get("tamanio"));
        result.put("fechaCarga", row.get("fecha_carga"));
        result.put("contenidoSimulado", row.get("contenido_simulado"));
        result.put("uriPath", row.get("uri_path"));
        return result;
    }

    @GetMapping("/pacientes")
    public List<Map<String, Object>> pacientes() {
        return getRecords("pacientes").stream().map(this::mapPaciente).toList();
    }

    @PostMapping("/pacientes")
    public Map<String, Object> crearPaciente(@RequestBody Map<String, Object> paciente) {
        return mapPaciente(createRecord("pacientes", paciente));
    }

    @GetMapping("/roles")
    public List<Map<String, Object>> roles() {
        return getRecords("roles");
    }

    @GetMapping("/sucursales")
    public List<Map<String, Object>> sucursales() {
        return getRecords("sucursales");
    }

    @PostMapping("/auth/users")
    public Map<String, Object> crearCuenta(@RequestBody Map<String, Object> request) {
        return auth("users", request);
    }

    @PostMapping("/auth/sessions")
    public Map<String, Object> iniciarSesion(@RequestBody Map<String, Object> request) {
        return auth("sessions", request);
    }

    @PostMapping("/auth/logout")
    public Map<String, Object> cerrarSesion(@RequestHeader(value = "Authorization", required = false) String authorization) {
        HttpHeaders requestHeaders = headers(false);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            requestHeaders.set("Authorization", authorization);
        }
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "api/auth/logout",
                HttpMethod.POST,
                new HttpEntity<>(Map.of(), requestHeaders),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() == null ? Map.of() : response.getBody();
    }

    @GetMapping("/usuarios")
    public List<Map<String, Object>> usuarios() {
        return getRecords("usuarios").stream().map(this::mapUsuario).toList();
    }

    @PostMapping("/usuarios")
    public Map<String, Object> crearUsuario(@RequestBody Map<String, Object> usuario) {
        Map<String, Object> remote = new LinkedHashMap<>(usuario);
        copyIfPresent(usuario, remote, "fotoPerfilUri", "foto_perfil_uri");
        remote.remove("rol");
        remote.remove("sucursal");
        remote.remove("fotoPerfilUri");
        return mapUsuario(createRecord("usuarios", remote));
    }

    @GetMapping("/atenciones")
    public List<Map<String, Object>> atenciones() {
        return getRecords("atenciones").stream().map(this::mapAtencion).toList();
    }

    @PostMapping("/atenciones")
    public Map<String, Object> crearAtencion(@RequestBody Map<String, Object> atencion) {
        Map<String, Object> remote = new LinkedHashMap<>();
        copyAny(atencion, remote, "paciente_id", "pacienteId", "paciente_id");
        copyAny(atencion, remote, "medico_id", "medicoId", "medico_id");
        copyAny(atencion, remote, "sucursal_id", "sucursalId", "sucursal_id");
        copyIfPresent(atencion, remote, "fecha", "fecha");
        copyIfPresent(atencion, remote, "motivo", "motivo");
        copyIfPresent(atencion, remote, "estado", "estado");
        return mapAtencion(createRecord("atenciones", remote));
    }

    @GetMapping("/examenes")
    public List<Map<String, Object>> examenes() {
        return getRecords("examenes").stream().map(this::mapExamen).toList();
    }

    @PostMapping("/examenes")
    public Map<String, Object> crearExamen(@RequestBody Map<String, Object> examen) {
        Map<String, Object> remote = new LinkedHashMap<>();
        copyAny(examen, remote, "paciente_id", "pacienteId", "paciente_id");
        copyAny(examen, remote, "medico_id", "medicoId", "medico_id");
        copyAny(examen, remote, "tipo_examen", "tipoExamen", "tipo_examen");
        copyAny(examen, remote, "ojo_evaluado", "ojoEvaluado", "ojo_evaluado");
        copyAny(examen, remote, "fecha_examen", "fechaExamen", "fecha_examen");
        copyAny(examen, remote, "fecha_resolucion", "fechaResolucion", "fecha_resolucion");
        copyIfPresent(examen, remote, "estado", "estado");
        copyIfPresent(examen, remote, "estadoEntrega", "estado_entrega");
        copyAny(examen, remote, "descripcion", "comentarios", "descripcion");
        copyIfPresent(examen, remote, "sucursal", "sucursal");
        return mapExamen(createRecord("examenes", remote));
    }

    @PutMapping("/examenes/{id}")
    public Map<String, Object> actualizarExamen(@PathVariable Long id, @RequestBody Map<String, Object> examen) {
        Map<String, Object> remote = new LinkedHashMap<>();
        copyAny(examen, remote, "paciente_id", "pacienteId", "paciente_id");
        copyAny(examen, remote, "medico_id", "medicoId", "medico_id");
        copyAny(examen, remote, "tipo_examen", "tipoExamen", "tipo_examen");
        copyAny(examen, remote, "ojo_evaluado", "ojoEvaluado", "ojo_evaluado");
        copyAny(examen, remote, "fecha_examen", "fechaExamen", "fecha_examen");
        copyAny(examen, remote, "fecha_resolucion", "fechaResolucion", "fecha_resolucion");
        copyIfPresent(examen, remote, "estado", "estado");
        copyIfPresent(examen, remote, "estadoEntrega", "estado_entrega");
        copyAny(examen, remote, "descripcion", "comentarios", "descripcion");
        copyIfPresent(examen, remote, "sucursal", "sucursal");

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                recordsUrl("examenes") + "?id=eq." + id,
                HttpMethod.PATCH,
                new HttpEntity<>(remote, headers(true)),
                new ParameterizedTypeReference<>() {}
        );
        List<Map<String, Object>> rows = response.getBody();
        return rows == null || rows.isEmpty() ? examen : mapExamen(rows.get(0));
    }

    @GetMapping("/documentos")
    public List<Map<String, Object>> documentos() {
        return getRecords("documentos_examen").stream().map(this::mapDocumento).toList();
    }

    @PostMapping("/documentos")
    public Map<String, Object> crearDocumento(@RequestBody Map<String, Object> documento) {
        Map<String, Object> remote = new LinkedHashMap<>();
        copyAny(documento, remote, "examen_id", "examenId", "examen_id");
        copyAny(documento, remote, "nombre_archivo", "nombreArchivo", "nombre_archivo");
        copyAny(documento, remote, "tipo_archivo", "tipoArchivo", "tipo_archivo");
        copyIfPresent(documento, remote, "tamanio", "tamanio");
        copyAny(documento, remote, "fecha_carga", "fechaCarga", "fecha_carga");
        copyAny(documento, remote, "contenido_simulado", "contenidoSimulado", "contenido_simulado");
        copyAny(documento, remote, "uri_path", "uriPath", "uri_path");
        return mapDocumento(createRecord("documentos_examen", remote));
    }

    private void copyIfPresent(Map<String, Object> source, Map<String, Object> target, String sourceKey, String targetKey) {
        if (source.containsKey(sourceKey) && source.get(sourceKey) != null) {
            target.put(targetKey, source.get(sourceKey));
        }
    }

    private void copyAny(Map<String, Object> source, Map<String, Object> target, String firstKey, String secondKey, String targetKey) {
        if (source.containsKey(firstKey)) {
            copyIfPresent(source, target, firstKey, targetKey);
        } else {
            copyIfPresent(source, target, secondKey, targetKey);
        }
    }
}
