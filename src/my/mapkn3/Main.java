package my.mapkn3;

import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.print("\033[2J");
        System.out.flush();
        var cells = Set.of(new Cell(5, 5), new Cell(1, 0), new Cell(2, 1), new Cell(0, 2), new Cell(1, 2), new Cell(2, 2));
        gen(cells, 100);
        System.out.print("\033[H");
        System.out.flush();
    }

    public record Cell(int x, int y) {
        public Stream<Cell> nb() {
            return IntStream.range(x() - 1, x() + 2)
                    .boxed()
                    .flatMap(x -> IntStream.range(y() - 1, y() + 2)
                            .mapToObj(y -> new Cell(x, y)))
                    .filter(not(this::equals));
        }

        public boolean alive(Set<Cell> cells) {
            var count = nb().filter(cells::contains).count();
            return (cells.contains(this) && count == 2) || count == 3;
        }
    }

    public static Set<Cell> evolve(Set<Cell> cells) {
        return cells.stream()
                .flatMap(Cell::nb)
                .distinct()
                .filter(c -> c.alive(cells))
                .collect(toSet());
    }

    public static void print(Set<Cell> cells) throws InterruptedException {
        System.out.print("\033[H");
        System.out.flush();
        var min = new Cell(cells.stream().mapToInt(Cell::x).min().getAsInt(),
                cells.stream().mapToInt(Cell::y).min().getAsInt());
        var max = new Cell(cells.stream().mapToInt(Cell::x).max().getAsInt(),
                cells.stream().mapToInt(Cell::y).max().getAsInt());

        range(min.y(), max.y() + 1)
                .mapToObj(y -> range(min.x(), max.x() + 1)
                        .mapToObj(x -> cells.contains(new Cell(x, y)) ? "X" : ".")
                        .collect(joining("")))
                .forEach(System.out::println);
        Thread.sleep(100);

        cells.forEach(System.out::println);
    }

    public static void gen(Set<Cell> cells, int steps) throws InterruptedException {
        print(cells);
        if (steps > 0) {
            gen(evolve(cells), steps - 1);
        }
    }
}
