import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';

// Keep even the temporary browser debugger in this worktree's 7xxx port range.
const launch = Browser.launch;
Browser.launch = function (options = {}) {
    return launch.call(this, {...options, extraArgs: ['--remote-debugging-port=7301', '--no-sandbox', '--disable-gpu']});
};
const newPage = Browser.prototype.newPage;
Browser.prototype.newPage = async function (options = {}) {
    const page = await newPage.call(this, options);
    await page.send('Emulation.setEmulatedMedia', {features: [
        {name: 'prefers-color-scheme', value: process.env.SITE_THEME || 'light'},
        {name: 'prefers-reduced-motion', value: options.reducedMotion ? 'reduce' : 'no-preference'},
    ]});
    return page;
};
await import('file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/inspect.mjs');
