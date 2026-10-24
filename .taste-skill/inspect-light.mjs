// Run the unmodified skill inspector with an emulated light OS preference.
import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';
const newPage = Browser.prototype.newPage;
Browser.prototype.newPage = async function(options) {
    const page = await newPage.call(this, options);
    const features = [{name: 'prefers-color-scheme', value: 'light'}];
    if (options?.reducedMotion) features.push({name: 'prefers-reduced-motion', value: 'reduce'});
    await page.send('Emulation.setEmulatedMedia', {features});
    return page;
};
process.argv = [process.argv[0], 'inspect.mjs', 'http://localhost:6101/', '--out', '.taste-skill/after-light', '--allow', 'violet'];
await import('file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/inspect.mjs');
