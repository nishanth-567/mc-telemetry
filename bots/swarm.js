const mineflayer = require('mineflayer');
const sleep = ms => new Promise(r => setTimeout(r, ms));

const BOT_COUNT = +(process.env.BOT_COUNT || 5);
const STAGGER_MS = 5000; // 5-second gap between each bot joining

function launch(i) {
  const b = mineflayer.createBot({
    host: process.env.MC_HOST || 'localhost',
    port: +(process.env.MC_PORT || 25565),
    username: `TelemetryBot${i}`,
    auth: 'offline',
    version: '1.21.4'
  });

  b.once('spawn', () => {
    console.log(`${b.username} connected successfully!`);
    
    // Disable physics and fix the tick-end to prevent movement kicks
    if (b.physics) b.physics.enabled = false;
    
    b.on('physicTick', () => {
      try { b._client.write('tick_end', {}); } catch (e) {}
    });

    setInterval(() => {
      if (b && b.player) b.chat(`telemetry-ping-${i}`);
    }, 10000);
  });

  b.on('kicked', r => console.log(`${b.username} Kicked:`, typeof r === 'string' ? r : JSON.stringify(r)));
  b.on('error', e => console.log(`${b.username} error:`, e.message));
  b.on('end', () => {
    console.log(`${b.username} disconnected; reconnecting in 10 seconds...`);
    setTimeout(() => launch(i), 10000);
  });
}

async function main() {
  for (let i = 0; i < BOT_COUNT; i++) {
    launch(i);
    await sleep(STAGGER_MS); // Force the script to wait before spawning the next bot
  }
}

main();