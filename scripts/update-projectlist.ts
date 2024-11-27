import {writeFile} from 'node:fs/promises';
import path from 'node:path';

import projectList from '../project-list.json';

const arg = process.argv[2];

projectList.push(arg);
projectList.sort();

await writeFile(path.join(__dirname, '..', 'project-list.json'), JSON.stringify(projectList, null, 2));
