package my.mapkn3;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.print("\033[2J");
        System.out.flush();
//        var cells = Set.of(new Cell(6, 5), new Cell(7, 6), new Cell(5, 7), new Cell(6, 7), new Cell(7, 7));
        var cells = range(0, 20)
                .mapToObj(y -> range(0, 20)
                        .mapToObj(x -> new Cell(x, y))
                        .filter(ignore -> ThreadLocalRandom.current().nextBoolean())
                        .collect(Collectors.toSet()))
                .flatMap(Set::stream)
                .collect(toSet());
        gen(cells, 1000);
        System.out.print("\033[H");
        System.out.flush();
    }

    public record Cell(int x, int y) {
        public Stream<Cell> nb() {
            return range(x() - 1, x() + 2)
                    .boxed()
                    .flatMap(x -> range(y() - 1, y() + 2)
                            .mapToObj(y -> new Cell(x, y)))
                    .map(cell -> new Cell(normalize(cell.x), normalize(cell.y)))
                    .filter(not(this::equals));
        }

        public boolean alive(Set<Cell> cells) {
            var count = nb().filter(cells::contains).count();
            return (cells.contains(this) && count == 2) || count == 3;
        }
    }

    public static int normalize(int i) {
        if (i < 0) {
            return i + 20;
        }
        if (i >= 20) {
            return i - 20;
        }
        return i;
    }

    public static Set<Cell> evolve(Set<Cell> cells) {
        return cells.stream()
                .flatMap(Cell::nb)
                .distinct()
                .filter(c -> c.alive(cells))
                .collect(toSet());
    }

    public static void print(Set<Cell> cells, int steps) throws InterruptedException {
        System.out.print("\033[H");
        System.out.flush();
        System.out.printf("Generation: %05d | Alive: %05d\n", steps, cells.size());
        range(0, 20)
                .mapToObj(y -> range(0, 20)
                        .mapToObj(x -> cells.contains(new Cell(x, y)) ? "■" : "□")
//                        .mapToObj(x -> cells.contains(new Cell(x, y)) ? "■" : " ")
                        .collect(joining(" ")))
                .forEach(System.out::println);
        Thread.sleep(100);
    }

    public static void gen(Set<Cell> cells, int steps) throws InterruptedException {
        print(cells, steps);
        if (steps > 0) {
            gen(evolve(cells), steps - 1);
        }
    }
}
