import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';

const theme = process.argv.splice(2, 1)[0];
if (!['light', 'dark'].includes(theme)) throw new Error('First argument must be light or dark');
const original = Browser.prototype.newPage;
Browser.prototype.newPage = async function(options = {}) {
    const page = await original.call(this, options);
    const features = [{name:'prefers-color-scheme',value:theme}];
    if (options.reducedMotion) features.push({name:'prefers-reduced-motion',value:'reduce'});
    await page.send('Emulation.setEmulatedMedia',{features});
    return page;
};
await import('file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/inspect.mjs');
