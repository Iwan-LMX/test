import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;

// ------------------------------------------------------------------
// IwanBot
// ------------------------------------------------------------------
// A sample bot original made for Robocode by Mathew Nelson.
// Ported to Robocode Tank Royale by Flemming N. Larsen.
//
// Probably the first bot you will learn about.
// Moves in a seesaw motion, and spins the gun around at each end.
// ------------------------------------------------------------------
public class IwanBot extends Bot {
    // The main method starts our bot
    public static void main(String[] args) {
        new IwanBot().start();
    }

    // Constructor, which loads the bot config file
    IwanBot() {
        super(BotInfo.fromFile("IwanBot.json"));
    }

    // Called when a new round is started -> initialize and do some movement
    @Override
    public void run() {
        setBodyColor(Color.fromString("#21201f"));
        setTurretColor(Color.fromString("#d0201f"));
        setRadarColor(Color.fromString("#327a7a"));
        setScanColor(Color.fromString("#adf959"));
        // Repeat while the bot is running
        while (isRunning()) {
            setTurnLeft(90);
            forward(30);
        }
    }
    private void aimFire(double x, double y) {
        var bearingFromGun = gunBearingTo(x, y);
        // Turn the gun toward the scanned bot
        turnGunLeft(bearingFromGun);
        // If it is close enough, fire!
        if (Math.abs(bearingFromGun) <= 3 && getGunHeat() == 0) {
            fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
        }
        forward(30);
        // Generates another scan event if we see a bot.
        // We only need to call this if the gun (and therefore radar)
        // are not turning. Otherwise, scan is called automatically.
        if (bearingFromGun == 0) {
            rescan();
        }
    }
    // We saw another bot -> fire!
    @Override
    public void onScannedBot(ScannedBotEvent e) {
        aimFire(e.getX(), e.getY());
    }
    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Calculate the bearing to the direction of the bullet
        var bearingFromGun = calcGunBearing(e.getBullet().getDirection());

        turnGunLeft(bearingFromGun);
        // If it is close enough, fire!
        if (Math.abs(bearingFromGun) <= 3 && getGunHeat() == 0) {
            fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
        }
        forward(10);
    }
    // We hit another bot -> if it's our fault, we'll stop turning and moving,
    // so we need to turn again to keep spinning.
    @Override
    public void onHitBot(HitBotEvent e) {
        var direction = directionTo(e.getX(), e.getY());
        var bearing = calcBearing(direction);
        if (bearing > -10 && bearing < 10) {
            fire(3);
        }
        if (e.isRammed()) {
            turnLeft(10);
        }
    }
    // We won the round -> do a victory dance!
    @Override
    public void onWonRound(WonRoundEvent e) {
        // Victory dance turning right 360 degrees 100 times
        turnLeft(36_000);
    }

}
