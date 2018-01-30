package es.urjc.ia.bikesurbanfleets.core;

import es.urjc.ia.bikesurbanfleets.common.util.JsonValidation;
import es.urjc.ia.bikesurbanfleets.core.config.ConfigJsonReader;
import es.urjc.ia.bikesurbanfleets.core.config.SimulationConfiguration;
import es.urjc.ia.bikesurbanfleets.core.core.SimulationEngine;

import java.io.IOException;

import es.urjc.ia.bikesurbanfleets.systemmanager.SystemManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Hello world!
 *
 */

public class Application {
    
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    
    
    private static CommandLine commandParser(String[] args) throws ParseException {
        
        Options options = new Options();
        options.addOption("schema", true, "Directory to schema validation");
        options.addOption("config", true, "Directory to the configuration file");
        options.addOption("validator", true, "Directory to the js validator");
    
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
        
    }
    
    public static void main(String[] args) throws ParseException {
        
        CommandLine cmd;
        try {
            cmd = commandParser(args);
        } catch (ParseException e1) {
            System.out.println("Error reading params");
            throw e1;
        }
        
        String schema = cmd.getOptionValue("schema");
        String config = cmd.getOptionValue("config");
        String validator = cmd.getOptionValue("validator");
        
        if(checkParams(schema, config, validator)) {
            ConfigJsonReader jsonReader = new ConfigJsonReader(config);
            try {
                SimulationConfiguration simulationConfiguration = jsonReader.createSimulationConfiguration();
                SystemManager systemManager = jsonReader.createSystemManager(simulationConfiguration);
                SimulationEngine simulation = new SimulationEngine(simulationConfiguration, systemManager);
                simulation.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkParams(String schema, String config, String validator) {
        boolean result = false;
        if(validator != null && schema != null && config != null) {
            try {
                String resultValidation = JsonValidation.validate(schema, config, validator);
                if(!resultValidation.equals("OK") && !resultValidation.equals("NODE_NOT_INSTALLED")) {
                    System.out.println(ANSI_RED +"JSON has errors" + ANSI_RESET);
                    System.out.println(resultValidation);
                    result = true;
                    return false;
                } else if (resultValidation.equals("NODE_NOT_INSALLED")) {
                    System.out.println(ANSI_RED + "Node is necessary to execute validator: " + validator + ". \n"
                            + "Verify if node is installed or install node" + ANSI_RESET);
                } else {
                    System.out.println(ANSI_GREEN + "Validation configuration input:" + resultValidation + ANSI_RESET);
                    result = true;
                }
            } catch (IOException | InterruptedException e) {
                System.out.println(ANSI_RED + "Fail executing validation" + ANSI_RESET);
                e.printStackTrace();
            }
        }
        else if(config == null) {
            System.out.println(ANSI_RED + "You should specify a configuration file" + ANSI_RESET);
            result = false;
        }
        else if(validator == null) {
            System.out.println(ANSI_YELLOW + "Warning, you don't specify a validator, configuration file will not be validated"
                    + " on backend" + ANSI_RESET);
            result = true;
        }
        return result;
    }
    
}