package com.dinnerbone.bukkit.scrap;

/**
 * Thrown when there was an error processing a command.
 * 
 * @author sk89q
 */
public abstract class CommandException extends Exception {
    
    private static final long serialVersionUID = 2347702186292859490L;
    
    public CommandException() {
    }
    
    public CommandException(String msg) {
        super(msg);
    }
    
}
