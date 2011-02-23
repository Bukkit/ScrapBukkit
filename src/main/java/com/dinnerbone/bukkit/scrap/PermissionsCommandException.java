package com.dinnerbone.bukkit.scrap;

/**
 * Thrown when permissions were not qualified for processing a command.
 *
 * @author sk89q
 */
public class PermissionsCommandException extends CommandException {
    
    private static final long serialVersionUID = -510328527240346074L;

    public PermissionsCommandException() {
        super();
    }

    public PermissionsCommandException(String msg) {
        super(msg);
    }

}
