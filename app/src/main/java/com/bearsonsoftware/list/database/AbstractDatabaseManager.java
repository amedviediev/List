package com.bearsonsoftware.list.database;

import java.util.ArrayList;

/**
 * common parent class for database managers
 */
public abstract class AbstractDatabaseManager {

    public abstract void open();
    public abstract void close();
    public abstract void updatePositions(ArrayList list);
}
