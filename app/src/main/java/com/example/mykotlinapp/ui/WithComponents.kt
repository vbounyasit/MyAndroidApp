package com.example.mykotlinapp.ui

/**
 * Interface implemented by fragments/activities to register necessaries components
 */
interface WithComponents {

    /**
     * Registers the various livedata observers for your UI
     */
    fun registerObservers() {}

    /**
     * Registers the various listeners for your UI elements
     */
    fun registerListeners() {}

    /**
     * Registers the various UI components such as menus, dialogs, etc
     */
    fun registerUIComponents() {}

    /**
     * Registers all the necessary components (usually used in the onCreate or onCreateView method of fragments/activities)
     */
    fun registerComponents() {
        registerListeners()
        registerUIComponents()
        registerListeners()
    }
}