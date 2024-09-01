package puzzles.astro.solver;

import puzzles.astro.model.AstroConfig;
import puzzles.astro.model.AstroModel;
import puzzles.common.Direction;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Astro {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Astro filename");
        } else{
            try {
                AstroModel model = new AstroModel(args[0]);
                AstroConfig config = new AstroConfig(args[0]);

                model.setCurrentConfig(config);
                List<Configuration> solution = Solver.solve(config);

                if(solution != null){
                    System.out.println("Total configs: " +  Solver.getTotalConfigs());
                    System.out.println("Unique configs: " + Solver.getUniqueConfigs());

                    for (int i = 0; i < solution.size(); i++) {
                        System.out.println("Step " + i + ": ");
                        System.out.println(solution.get(i));
                    }
                }
                
                else{
                    System.out.println("No solution");
                }

            }
            catch (IOException e){
                System.out.println("File not found. ");
            }
        }
    }
}
