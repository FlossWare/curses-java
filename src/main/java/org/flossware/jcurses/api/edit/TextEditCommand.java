package org.flossware.jcurses.api.edit;

/**
 * Command pattern interface for text editing operations that support undo/redo.
 */
public interface TextEditCommand {
    /**
     * Execute this command.
     */
    void execute();

    /**
     * Undo this command, restoring previous state.
     */
    void undo();

    /**
     * Returns true if this command can be merged with another command of the same type.
     * Used to merge consecutive character insertions or deletions.
     */
    default boolean canMergeWith(TextEditCommand other) {
        return false;
    }

    /**
     * Merge this command with another command.
     * Only called if canMergeWith returns true.
     */
    default void mergeWith(TextEditCommand other) {
        // Default: no merging
    }
}
