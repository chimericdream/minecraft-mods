import {readFile, writeFile} from 'node:fs/promises';
import path from 'node:path';

import projectList from '../project-list.json';

const settingsTemplate = (await readFile(path.join(__dirname, 'settings.gradle.tpl'))).toString();

const settingsGradle = settingsTemplate.replace(
    '{{PROJECT_LIST}}',
    JSON.stringify(projectList, null, 4).replaceAll('"', "'")
);

await writeFile(path.join(__dirname, '..', 'settings.gradle'), settingsGradle);
