package es.mariana.dweb.controller;

import es.mariana.dweb.model.entities.Pokemon;
import es.mariana.dweb.model.entities.PokemonHeightComparator;
import es.mariana.dweb.model.entities.PokemonNameComparator;
import es.mariana.dweb.model.entities.PokemonWeightComparator;
import es.mariana.dweb.model.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

@Controller
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @GetMapping("/")
    public String home(Model model) {
        ArrayList<Pokemon> pokemons = pokemonService.obtenerPokemons();
        model.addAttribute("pokemons", pokemons);

        return "home";
    }

    @GetMapping("/informacion")
    public String informacion(Model model) {
        Pokemon pokemonDestacado = pokemonService.findRandom();

        model.addAttribute("titulo", "Pokémon destacado");
        model.addAttribute(
                "presentacion",
                "Conoce los datos principales de uno de los Pokémon disponibles en nuestra Pokédex."
        );
        model.addAttribute("fechaHora", LocalDateTime.now());
        model.addAttribute("pokemonDestacado", pokemonDestacado);

        return "informacion";
    }

    @GetMapping("/pokemon/tabla")
    public String mostrarTabla(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Double alturaMin,
            @RequestParam(required = false) Double pesoMax,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String sentido,
            Model model) {

        ArrayList<Pokemon> pokemons = recuperarPokemons(tipo, 10);
        pokemons = filtrarYOrdenar(pokemons, nombre, alturaMin, pesoMax, orden, sentido);

        model.addAttribute("titulo", "Pokémon en formato tabla");
        model.addAttribute("fechaHora", LocalDateTime.now());
        model.addAttribute("pokemons", pokemons);
        model.addAttribute("esTabla", true);

        addAtributosAlFormulario(model, tipo, nombre, alturaMin, pesoMax, orden, sentido);

        return "listado-tabla";
    }

    @GetMapping("/pokemon/galeria")
    public String mostrarGaleria(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Double alturaMin,
            @RequestParam(required = false) Double pesoMax,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String sentido,
            Model model) {

        ArrayList<Pokemon> pokemons = recuperarPokemons(tipo, 12);
        pokemons = filtrarYOrdenar(pokemons, nombre, alturaMin, pesoMax, orden, sentido);

        model.addAttribute("titulo", "Galería Pokémon");
        model.addAttribute("pokemons", pokemons);
        model.addAttribute("esTabla", false);

        addAtributosAlFormulario(model, tipo, nombre, alturaMin, pesoMax, orden, sentido);

        return "galeria-pokemon";
    }

    @GetMapping("/pokemon/batalla")
    public String batalla(Model model){
        Pokemon pokemon1 = pokemonService.findRandom();
        Pokemon pokemon2 = pokemonService.findRandom();
        model.addAttribute("pokemon1",pokemon1);
        model.addAttribute("pokemon2",pokemon2);
        return "batalla-pokemon";
    }

    @GetMapping("/pokemon/{id}")
    public String detallePokemon(@PathVariable int id, Model model) {
        Pokemon pokemon = pokemonService.findById(id);
        model.addAttribute("titulo", "Detalle Pokémon");
        model.addAttribute("presentacion", "Consulta los datos principales de este Pokémon.");
        model.addAttribute("fechaHora", LocalDateTime.now());
        model.addAttribute("pokemonDestacado", pokemon);
        return "informacion";
    }

    private ArrayList<Pokemon> recuperarPokemons(String tipo, int tamano) {
        if (tipo != null && !tipo.isBlank()) {
            return pokemonService.findByType(tipo);
        }
        return pokemonService.findRandomList(tamano);
    }

    private ArrayList<Pokemon> filtrarYOrdenar(
            ArrayList<Pokemon> lista,
            String nombre,
            Double alturaMin,
            Double pesoMax,
            String orden,
            String sentido) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            ArrayList<Pokemon> filtrados = new ArrayList<>();

            for (Pokemon pokemon : lista) {
                if (pokemon.getName() != null && pokemon.getName()
                                                .toLowerCase()
                                                .contains(nombre.toLowerCase().trim())) {
                    filtrados.add(pokemon);
                }
            }
            lista = filtrados;
        }

        if (alturaMin != null) {
            ArrayList<Pokemon> filtrados = new ArrayList<>();

            for (Pokemon pokemon : lista) {
                if (pokemon.getHeight() >= alturaMin * 10) {
                    filtrados.add(pokemon);
                }
            }

            lista = filtrados;
        }

        if (pesoMax != null) {
            ArrayList<Pokemon> filtrados = new ArrayList<>();

            for (Pokemon pokemon : lista) {
                if (pokemon.getWeight() <= pesoMax * 10) {
                    filtrados.add(pokemon);
                }
            }

            lista = filtrados;
        }

        if (orden != null && !orden.isEmpty()) {
            Comparator<Pokemon> comparador = null;
            switch (orden) {
                case "nombre":
                    comparador = new PokemonNameComparator();
                    break;
                case "altura":
                    comparador = new PokemonHeightComparator();
                    break;
                case "peso":
                    comparador = new PokemonWeightComparator();
                    break;
            }

            if (comparador != null) {
                if ("desc".equals(sentido)) {
                    comparador = comparador.reversed();
                }
                lista.sort(comparador);
            }
        }

        return lista;
    }
    private void addAtributosAlFormulario(Model model, String tipo, String nombre, Double alturaMin, Double pesoMax, String orden, String sentido) {
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("nombre", nombre);
        model.addAttribute("alturaMin", alturaMin);
        model.addAttribute("pesoMax", pesoMax);
        model.addAttribute("orden", orden);
        model.addAttribute("sentido", sentido);
        model.addAttribute("tipos", pokemonService.findAllTypes());
    }
}
