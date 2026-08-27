package com.chimericdream.modstatus;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor.ColoredFragment;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.awt.Color;

/**
 * Appends each mod folder's version to its Project View row, in the same muted style the platform
 * uses for labels like "library root", plus an amber dot when that mod has unreleased changelog
 * entries.
 *
 * <p>The version is appended as a coloured text fragment rather than via
 * {@link PresentationData#setLocationString}, because {@code NodeRenderer} renders location strings
 * with a hard-coded {@link SimpleTextAttributes#GRAYED_ATTRIBUTES} -- there would be no way to make
 * the "unreleased" marker stand out. Fragments give per-run control over colour.
 */
public final class ModStatusNodeDecorator implements ProjectViewNodeDecorator {
    /** Separates the folder name from the version. Two spaces reads better than the usual one. */
    private static final String VERSION_PREFIX = "  ";

    private static final String UNRELEASED_MARKER = " ●";

    private static final SimpleTextAttributes UNRELEASED_ATTRIBUTES =
        new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, UnreleasedBadgeIcon.COLOR);

    @Override
    public void decorate(@NotNull ProjectViewNode<?> node, @NotNull PresentationData data) {
        Project project = node.getProject();

        if (project == null || project.isDisposed()) {
            return;
        }

        VirtualFile file = node.getVirtualFile();

        if (file == null || !file.isDirectory()) {
            return;
        }

        ModStatus status = project.getService(ModStatusService.class).getStatus(file);

        if (status == null) {
            return;
        }

        String versionText = VERSION_PREFIX + status.version();

        if (alreadyDecorated(data, versionText)) {
            return;
        }

        // PsiDirectoryNode builds content-root rows out of coloured fragments (that is where the bold
        // comes from), but plain directories only get a presentable text plus an attributes key.
        // Appending to an empty fragment list would silently drop the name, so seed it when empty.
        if (data.getColoredText().isEmpty()) {
            String name = data.getPresentableText();
            data.addText(name == null ? file.getName() : name, baseAttributes(data));
        }

        data.addText(versionText, SimpleTextAttributes.GRAYED_ATTRIBUTES);

        if (status.hasUnreleasedChanges()) {
            data.addText(UNRELEASED_MARKER, UNRELEASED_ATTRIBUTES);
            data.setIcon(withBadge(data.getIcon(false)));
            data.setTooltip(status.modId() + " " + status.version() + " — has unreleased changes");
        } else {
            data.setTooltip(status.modId() + " " + status.version());
        }
    }

    /**
     * Guards against appending twice if the same {@link PresentationData} is passed through the
     * decorator chain more than once.
     */
    private static boolean alreadyDecorated(@NotNull PresentationData data, @NotNull String versionText) {
        for (ColoredFragment fragment : data.getColoredText()) {
            if (versionText.equals(fragment.getText())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reproduces how {@code NodeRenderer} would have coloured the node name, so seeding the fragment
     * list does not flatten states the platform expresses through the attributes key (excluded
     * folders, ignored files) or a forced foreground.
     */
    private static @NotNull SimpleTextAttributes baseAttributes(@NotNull PresentationData data) {
        SimpleTextAttributes base = SimpleTextAttributes.REGULAR_ATTRIBUTES;
        TextAttributesKey key = data.getTextAttributesKey();

        if (key != null) {
            TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key);

            if (attributes != null) {
                base = SimpleTextAttributes.fromTextAttributes(attributes);
            }
        }

        Color forcedForeground = data.getForcedTextForeground();

        if (forcedForeground != null) {
            base = new SimpleTextAttributes(base.getStyle(), forcedForeground);
        }

        return base;
    }

    /**
     * Overlays the badge on the top-right of the folder icon.
     *
     * <p>A {@code RowIcon} would sit beside the folder instead, which widens the row and knocks mod
     * folders out of alignment with everything else in the tree. The top-right corner is used rather
     * than the more conventional bottom-right because that is where the platform already draws its
     * own "module content root" marker -- every active mod is a Gradle module, so a bottom-right
     * badge would cover that marker on exactly the folders it matters most for.
     */
    private static @NotNull Icon withBadge(@Nullable Icon base) {
        if (base == null) {
            return UnreleasedBadgeIcon.INSTANCE;
        }

        Icon badge = UnreleasedBadgeIcon.INSTANCE;
        LayeredIcon layered = new LayeredIcon(2);

        layered.setIcon(base, 0);
        layered.setIcon(badge, 1, base.getIconWidth() - badge.getIconWidth(), 0);

        return layered;
    }
}
