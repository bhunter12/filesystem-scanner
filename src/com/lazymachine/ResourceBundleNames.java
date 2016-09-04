package com.lazymachine;

// Here the resource bundles are defined by module so that we have one resource bundle per module rather than separate
// bundles for different contents like Messages or Errors
public enum ResourceBundleNames {

    LAZYMACHINE("com.lazymachine.resources.Lazymachine"),
    FILE_SYSTEM("com.lazymachine.filesystem.resources.FileSystem");

    private final String bundleName;

    ResourceBundleNames(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleName() {
        return bundleName;
    }

    @Override
    public String toString() {
        return bundleName;
    }

}
