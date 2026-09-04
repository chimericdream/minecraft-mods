package com.chimericdream.modstatus;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Keeps the decoration honest: editing a mod's {@code gradle.properties} or {@code CHANGELOG.md}
 * drops that folder's cached status and refreshes the Project View, so bumping {@code mod_version}
 * or adding an "Unreleased changes" entry shows up without reopening the project.
 */
final class ModStatusFileListener implements BulkFileListener {
    private static final Set<String> WATCHED_FILES = Set.of("gradle.properties", "CHANGELOG.md");

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        Set<String> changedDirectories = new HashSet<>();

        for (VFileEvent event : events) {
            String path = event.getPath();

            if (WATCHED_FILES.contains(PathUtil.getFileName(path))) {
                changedDirectories.add(PathUtil.getParentPath(path));
            }
        }

        if (changedDirectories.isEmpty()) {
            return;
        }

        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (project.isDisposed()) {
                continue;
            }

            // Only projects that have actually decorated a node have a service worth invalidating.
            ModStatusService service = project.getServiceIfCreated(ModStatusService.class);

            if (service == null) {
                continue;
            }

            changedDirectories.forEach(service::invalidate);

            ApplicationManager.getApplication().invokeLater(
                () -> ProjectView.getInstance(project).refresh(),
                project.getDisposed()
            );
        }
    }
}
