export type ModInfo = {
    folder: string;
    id: string;
    name: string;
    description: string;
    version: string;
    isPrerelease: boolean;
    hasUnreleasedChanges: boolean;
    isNewMod: boolean;
    latestTag: string | null;
    lastCommit: string | null;
    /** Absolute path of the mod's icon.png, if it has one. */
    iconFile: string | null;
    /** Mod loaders named by the README's `modloader-*` badges, e.g. `Fabric`, `NeoForge`. */
    loaders: string[];
    /** The README's italic one-line tagline, if it has one. */
    tagline: string | null;
    /** CurseForge project id from gradle.properties, used for the download link. */
    curseforgeId: string | null;
};

export type IdeasInfo = {
    folder: string;
    hasReadme: boolean;
    identityDraft: boolean;
    themes: {name: string; count: number}[];
    ideaCount: number;
    /** Newest brainstorm session and how far through the pipeline it got. */
    latestBrainstorm: {name: string; stage: 'raw' | 'combined' | 'shortlist'} | null;
};

export type GithubItem = {
    kind: 'issue' | 'pr';
    number: number;
    title: string;
    url: string;
    labels: string[];
    author: string;
    createdAt: string;
    isDraft?: boolean;
    checks?: 'passing' | 'failing' | 'pending' | 'none';
    /** Folder names of the mods this item is about; empty means unassigned. */
    mods: string[];
};

export type GithubData = {fetchedAt: string; items: GithubItem[]};

export type GithubResult = {ok: true; data: GithubData} | {ok: false; error: string};

export type DashboardData = {
    generatedAt: string;
    mods: ModInfo[];
    ideas: Record<string, IdeasInfo>;
    github: GithubResult;
};
