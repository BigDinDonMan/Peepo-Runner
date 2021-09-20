package com.peeporunner.ui.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.peeporunner.PeepoRunnerGame
import com.peeporunner.ecs.components.AnimationComponent.AnimationStateData
import com.peeporunner.ecs.components.TextureComponent
import com.peeporunner.ecs.components.gamelogic.CoinType
import com.peeporunner.ecs.components.gamelogic.PeepoPlayerComponent
import com.peeporunner.ecs.components.mappers.CompMappers
import com.peeporunner.ecs.components.physics.AttackRayCastCallback
import com.peeporunner.ecs.components.physics.CollisionListener
import com.peeporunner.ecs.input.PeepoInputController
import com.peeporunner.ecs.systems.*
import com.peeporunner.ui.widgets.CoinCounter
import com.peeporunner.util.*
import com.peeporunner.util.timers.GameTimer
import com.peeporunner.util.timers.RandomizedGameTimer
import kotlinx.coroutines.*
import kotlin.math.pow
import kotlin.properties.Delegates
import kotlin.random.Random

//todo: change screen class locations to not be in the UI section
@Suppress("UNCHECKED_CAST")
class PeepoGameScreen(private val game: PeepoRunnerGame, val spriteBatch: SpriteBatch, val uiBatch: SpriteBatch, private val engine: Engine, private val world: World, private val assetManager: AssetManager) : ScreenAdapter(), ApplicationListener {

    private val playerStartPosition = Pair(60f, 300f)
    private val entityPool = EntityPool(15, 75)
    private val playerEntity = entityPool.obtain()

    private val animationSystem = AnimationSystem()
    private val renderSystem = RenderSystem(spriteBatch)
    private val physicsSystem = PhysicsSystem(world)
    private val velocitySystem = EnvironmentVelocitySystem()
    private val sineMovementSystem = SineMovementSystem()
    private val debugPhysicsSystem = PhysicsDebugRenderSystem(world, renderSystem.camera)

    private val viewport = StretchViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), renderSystem.camera)

    private lateinit var countDownLabel: Label
    private lateinit var coinCounter: CoinCounter
    private lateinit var scoreLabel: Label
    private lateinit var pauseButton: TextButton
    private lateinit var attackButton: ImageTextButton
    private val pauseGameDialog by lazy {
        object : Dialog("Paused", Skin(Gdx.files.internal("skins/uiskin.json"))){
            override fun result(`object`: Any?) {
                (`object` as? Action)?.invoke()
            }
        }.apply { /*this skin is a subject to change (if I finally buckle up and make my own graphics)*/
            isModal = false
            isMovable = false
        }.button("Resume", { paused = false }).
        button("Exit", this@PeepoGameScreen::exitToMenu)
    }
    private val font = BitmapFont() //placeholder for countdown

    private val uiStage = Stage(viewport, uiBatch)

    private val peepoRunAtlas by lazy { assetManager.get("graphics/peeporun.atlas", TextureAtlas::class.java) }
    private val coinSound by lazy { assetManager.get("audio/coin-sound.wav", Sound::class.java) }
    private val coinTextures by lazy {
        mapOf(
                CoinType.COIN_1 to assetManager.get("graphics/pepecoin-brown.png", Texture::class.java),
                CoinType.COIN_5 to assetManager.get("graphics/pepecoin-silver.png", Texture::class.java),
                CoinType.COIN_10 to assetManager.get("graphics/pepecoin-gold.png", Texture::class.java),
                CoinType.COIN_25 to assetManager.get("graphics/pepecoin-platinum.png", Texture::class.java),
                CoinType.COIN_50 to assetManager.get("graphics/pepecoin-iridescent.png", Texture::class.java)
        )
    }

    private val coinValuesRandomizerList = ArrayList<CoinType>()

    private val removalService = EntityRemovalService(engine, entityPool, world)
    private val initializationService = DeferredEntityInitializationService(engine)

    private lateinit var generationCoroutine: Job

    var paused: Boolean by Delegates.observable(false) { _, _, newValue ->
        run {
            val callback = if (newValue) this@PeepoGameScreen::pauseGame else this@PeepoGameScreen::resumeGame
            callback.invoke()
            if (!uiStage.actors.contains(pauseGameDialog))
                uiStage.addActor(pauseGameDialog)
            if (newValue) pauseGameDialog.show(uiStage)
            else pauseGameDialog.hide()
        }
    }

    private val componentFactory = ComponentFactory(engine, world)

    companion object {
        private const val BASIC_ENVIRONMENT_SCROLL_SPEED = -350f // negative value because they are going towards left side of the screen
        private const val MAX_ENVIRONMENT_SCROLL_SPEED = -550f

        private const val PLAYER_SNAPBACK_SPEED = 62.5f

        private const val LOWER_GENERATION_INTERVAL = 700
        private const val UPPER_GENERATION_INTERVAL = 1100
        private const val MINIMUM_BUILDING_WIDTH = 150.0
        private const val MAXIMUM_BUILDING_WIDTH = 200.0
        private const val MINIMUM_BUILDING_HEIGHT = 150.0
        private const val MAXIMUM_BUILDING_HEIGHT = 350.0

        private const val MINIMUM_SINE_FREQUENCY = 2f
        private const val MAXIMUM_SINE_FREQUENCY = 4f
        private const val MINIMUM_SINE_AMPLITUDE = 2f
        private const val MAXIMUM_SINE_AMPLITUDE = 4f
    }

    private var currentGameSpeed = BASIC_ENVIRONMENT_SCROLL_SPEED
    private val gameSpeedUpMultiplier = 2f
    private var countPoints = false

    private val cooldownTimer: GameTimer by lazy {
        val playerComponent = CompMappers.playerComponentMapper.get(playerEntity)
        GameTimer(playerComponent.attackCooldown, {
            attackButton.touchable = Touchable.enabled
        }, {
            val color = attackButton.color
            val alpha = MathUtils.clamp(cooldownTimer.elapsedTime() / cooldownTimer.targetTime(), 0.35f, 1f)
            attackButton.setColor(color.r, color.g, color.b, alpha)
        })
    }

    private val generationTimer: RandomizedGameTimer by lazy {
        RandomizedGameTimer(LOWER_GENERATION_INTERVAL, UPPER_GENERATION_INTERVAL, true, this::performGenerationStep)
    }

    init {
        loadGameAssets()
        setUpCoinRandomizer()
        world.setContactListener(CollisionListener())
        setupUI()
        initEntitySystems()
        buildLevelStart()
        initPlayerEntity()
        startCountDownTimer()
    }

    private fun loadGameAssets() {
        assetManager.load("audio/coin-sound.wav", Sound::class.java)
        assetManager.load("audio/whip-snap.wav", Sound::class.java)
        assetManager.load("graphics/pepecoin-brown.png", Texture::class.java)
        assetManager.load("graphics/pepecoin-silver.png", Texture::class.java)
        assetManager.load("graphics/pepecoin-gold.png", Texture::class.java)
        assetManager.load("graphics/pepecoin-platinum.png", Texture::class.java)
        assetManager.load("graphics/pepecoin-iridescent.png", Texture::class.java)
        assetManager.finishLoading()
    }

    override fun render() = this.render(Gdx.graphics.deltaTime)

    override fun render(delta: Float) {
        engine.update(delta)
        updateGameScore()
        increaseGameSpeed(delta)
        snapBackPlayerIfMovedBack()
        deleteEntitiesOutsideScreen()
        removalService.process()
        initializationService.process()
        uiStage.act(delta)
        uiStage.draw()
        checkForGameOver()
        cooldownTimer.update(delta)
    }

    private fun snapBackPlayerIfMovedBack() {
        val transform = CompMappers.transformMapper.get(playerEntity)
        val physicsBodyComponent = CompMappers.physicsBodyMapper.get(playerEntity)

        physicsBodyComponent.body!!.setLinearVelocity(
                if (transform.position.x < playerStartPosition.first) PLAYER_SNAPBACK_SPEED else 0f,
                physicsBodyComponent.body!!.linearVelocity.y
        )
    }

    override fun create() {}

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)

    override fun pause() {
        paused = true
    }

    override fun resume() {
        paused = false
    }

    override fun dispose() {
        stopGenerationThread()
        engine.systems.forEach(engine::removeSystem)
        engine.entities.forEach { e ->
            kotlin.run {
                val physicsBodyComponent = CompMappers.physicsBodyMapper.get(e)
                if (physicsBodyComponent?.body != null) {
                    removalService.mark(physicsBodyComponent.body!!)
                }
            }
        }
        removalService.process()
        engine.entities.forEach(entityPool::free)
        engine.removeAllEntities()
        uiStage.dispose()
        font.dispose()
    }

    private fun setUpCoinRandomizer() {
        coinValuesRandomizerList += CoinType.values().reversed().withIndex().
            flatMap { (index, value) -> (0..index.toFloat().pow(2.5f).toInt()).map { value } }
    }

    private fun initPlayerEntity() {
        val animator = componentFactory.newAnimator(
                Pair("Run", AnimationStateData(Animation<TextureRegion>(0.05f, peepoRunAtlas.regions, Animation.PlayMode.LOOP)))
        ).apply { currentAnimKey = "Run" }
        val textureComponent = engine.createComponent(TextureComponent::class.java)
        val transform = componentFactory.newTransform(playerStartPosition.first, playerStartPosition.second,
                width = animator.currentFrame().regionWidth.toFloat(), height = animator.currentFrame().regionHeight.toFloat())
        val tagComponent = componentFactory.newTag("Player")

        val physicsBodyComponent = componentFactory.newBoxBody(
                BodyDef.BodyType.DynamicBody,
                transform.position.x + transform.size.x / 2 * transform.scale.x,
                transform.position.y + transform.size.y / 2 * transform.scale.y,
                fixedRotation = true,
                gravityScale = 70f,
                userData = playerEntity,
                boxWidth = transform.size.x,
                boxHeight = transform.size.y
        )

        val playerComponent = engine.createComponent(PeepoPlayerComponent::class.java).apply { playerJumpForce = 450f }

        val collisionDataComponent = componentFactory.newCollisionHandler(onCollisionEnter = { other -> kotlin.run {
            val tagComp = CompMappers.tagMapper.get(other)
            if (tagComp != null && tagComp.tag == "Jumping surface") {
                playerComponent.jumpCount = 0
                playerComponent.canJump = true
                velocitySystem.setProcessing(true)
                countPoints = true
            }
        }}, onCollisionExit = { other -> kotlin.run {
            val tagComp = CompMappers.tagMapper.get(other)
            if (tagComp != null && tagComp.tag == "Jumping surface") {
                // we cannot set the canJump flag here because it also checks it on the second jump
                // so the solution is to simulate that one jump was already performed
                playerComponent.jumpCount = 1
            }
        } })

        val audioComponent = componentFactory.newAudioComponent(
                "Attack" to assetManager.get("audio/whip-snap.wav", Sound::class.java)
        )

        playerEntity.addComponents(
                transform, animator, textureComponent, tagComponent, physicsBodyComponent,
                collisionDataComponent, playerComponent, audioComponent
        )

        engine.addEntity(playerEntity)
    }

    private fun createBuilding(x: Float, y: Float, width: Float, height: Float) {
        initializationService.queueEntity(entityPool.obtain()) { e -> kotlin.run {
            val transform = componentFactory.newTransform(x, y, width, height)

            val tagComponent = componentFactory.newTag("Building")

            val physicsBody = componentFactory.newBoxBody(
                    density = 0f, gravityScale = 0f, type = BodyDef.BodyType.KinematicBody,
                    initialX = transform.position.x + transform.size.x / 2 * transform.scale.x,
                    initialY = transform.position.y + transform.size.y / 2 * transform.scale.y,
                    userData = e, boxWidth = transform.size.x, boxHeight = transform.size.y
            )
            val collisionDataComponent = componentFactory.newCollisionHandler()

            val velocityComponent = componentFactory.newVelocityData(BASIC_ENVIRONMENT_SCROLL_SPEED)

            e.addComponents(transform, physicsBody, tagComponent, collisionDataComponent, velocityComponent)
            e
        } }
    }

    private fun createJumpingSurface(x: Float, y: Float, width: Float) {
        initializationService.queueEntity(entityPool.obtain()) { e -> kotlin.run {
            val transform = componentFactory.newTransform(x, y, width, 2.5f)
            val tagComponent = componentFactory.newTag("Jumping surface")

            val physicsBodyComponent = componentFactory.newBoxBody(
                    type = BodyDef.BodyType.KinematicBody,
                    initialX = transform.position.x + transform.size.x / 2 * transform.scale.x,
                    initialY = transform.position.y + transform.size.y / 2 * transform.scale.y,
                    gravityScale = 0f, density = 0f, userData = e,
                    boxHeight = transform.size.y, boxWidth = transform.size.x
            )

            val collisionDataComponent = componentFactory.newCollisionHandler()
            val velocityComponent = componentFactory.newVelocityData(BASIC_ENVIRONMENT_SCROLL_SPEED)

            e.addComponents(transform, physicsBodyComponent, tagComponent, collisionDataComponent, velocityComponent)
            e
        }}
    }

    private fun createCoin(x: Float, y: Float, sinAmplitude: Float, sinFrequency: Float) {
        initializationService.queueEntity(entityPool.obtain()) { e -> kotlin.run {
            val coinComponent = componentFactory.newCoinComponent(coinValuesRandomizerList[Random.nextInt(0, coinValuesRandomizerList.size)])

            val textureComponent = componentFactory.newTextureComponent(coinTextures[coinComponent.coinType]!!)

            val transform = componentFactory.newTransform(
                    x, y, textureComponent.textureRegion!!.regionWidth.toFloat(),
                    textureComponent.textureRegion!!.regionHeight.toFloat(), scaleX = 0.75f, scaleY = 0.75f).
            apply {
                originalPosition.set(x, y, originalPosition.z)
            }

            val physicsBodyComponent = componentFactory.newCircleBody(
                    type = BodyDef.BodyType.KinematicBody,
                    initialX = transform.position.x + transform.size.x / 2 * transform.scale.x,
                    initialY = transform.position.y + transform.size.y / 2 * transform.scale.y,
                    gravityScale = 0f, userData = e, isSensor = true,
                    radius = transform.size.x / 2 * transform.scale.x
            ).apply { freezeY = true }

            val audioComponent = componentFactory.newAudioComponent("Bling" to coinSound)

            val collisionDataComponent = componentFactory.newCollisionHandler(onCollisionEnter = { other -> run {
                val tagComp = CompMappers.tagMapper.get(other)
                if (tagComp != null && tagComp.tag == "Player") {
                    val playerComponent = CompMappers.playerComponentMapper.get(other)
                    playerComponent.currentPlayerCoins += coinComponent.coinType!!.coinValue
                    coinCounter.updateCounter(playerComponent.currentPlayerCoins)
                    audioComponent.play("Bling")
                    removalService.mark(e)
                    removalService.mark(CompMappers.physicsBodyMapper.get(e).body!!)
                }
            } })

            val tagComponent = componentFactory.newTag("Coin")
            val velocityComponent = componentFactory.newVelocityData(BASIC_ENVIRONMENT_SCROLL_SPEED)

            val sineComponent = componentFactory.newSineWaveData(sinAmplitude, sinFrequency)

            e.addComponents(transform, textureComponent, physicsBodyComponent, velocityComponent,
                            tagComponent, collisionDataComponent, coinComponent, sineComponent, audioComponent)
            e
        }}
    }

    private fun createEnemy(x: Float, y: Float, score: Int, sinAmplitude: Float, sinFrequency: Float) {
        initializationService.queueEntity(entityPool.obtain()) { e -> kotlin.run {
            val transform = componentFactory.newTransform(x, y, 50f, 50f) //temporary width + height
            val enemyComponent = componentFactory.newEnemyData(score)
            val animationComponent = componentFactory.newAnimator()
            val textureComponent = engine.createComponent(TextureComponent::class.java)
            val audioComponent = componentFactory.newAudioComponent()
            val bodyComponent = componentFactory.newBoxBody(BodyDef.BodyType.KinematicBody)
            val sineWaveComponent = componentFactory.newSineWaveData(sinAmplitude, sinFrequency)
            e.addComponents(transform, enemyComponent, animationComponent, textureComponent,
                    audioComponent, bodyComponent, sineWaveComponent)
            e
        } }
    }

    private fun deleteEntitiesOutsideScreen() {
        engine.entities.forEach { e ->
            run {
                if (e === playerEntity) {
                    return@run
                }
                val transform = CompMappers.transformMapper.get(e)
                if (transform.position.x < -transform.size.x)
                    removalService.mark(e)
            }
        }
    }

    private fun initEntitySystems() {
        engine.addSystem(physicsSystem)
        engine.addSystem(velocitySystem)
        engine.addSystem(sineMovementSystem)
        engine.addSystem(animationSystem)
        engine.addSystem(renderSystem)
        engine.addSystem(debugPhysicsSystem)
        engine.systems.forEach { s -> s.setProcessing(false) }
    }

    private fun startCountDownTimer() {
        val timer = Timer()
        timer.scheduleTask(object : Timer.Task() {
            private var callCount = 3
            override fun run() {
                countDownLabel.setText(callCount)
                callCount--
                if (callCount < 0) {
                    timer.stop()
                    countDownLabel.setText("")
                    engine.systems.forEach { s -> s.setProcessing(true) }
                    velocitySystem.setProcessing(false)
                    setupInputController()
                    launchGenerationThread()
                }
            }
        }, 0f, 1f)
        timer.start()
    }

    private fun setupInputController() {
        val peepoInputController = PeepoInputController(world, playerEntity, this)
        Gdx.input.inputProcessor = InputMultiplexer(uiStage, peepoInputController)
    }

    private fun setupUI() {
        val style = Label.LabelStyle()
        style.font = font
        countDownLabel = Label("", style)
        scoreLabel = Label("Score:\n0", style)
        scoreLabel.setAlignment(Align.top)
        scoreLabel.setPosition(Gdx.graphics.width.toFloat()/2 - scoreLabel.width / 2, Gdx.graphics.height.toFloat() - 65f)
        val coinTexture = coinTextures[CoinType.COIN_25]!!
        coinCounter = CoinCounter(coinTexture.resize(0.85f, 0.85f))
        coinCounter.setPosition(Gdx.graphics.width.toFloat() - coinCounter.width, Gdx.graphics.height.toFloat() - coinCounter.height)
        coinCounter.align(Align.topRight)
        countDownLabel.setPosition(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2)
        countDownLabel.setAlignment(Align.center)

        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = font
        pauseButton = TextButton("X", buttonStyle)
        pauseButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                paused = true
            }
        })
        pauseButton.width = 75f
        pauseButton.height = 75f
        pauseButton.setPosition(0f, Gdx.graphics.height - pauseButton.height)
        attackButton = ImageTextButton("Attack", Skin(Gdx.files.internal("skins/uiskin.json")))
        attackButton.addListener(object : ClickListener() {
            private val rayCastCallback = AttackRayCastCallback(playerEntity)
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                val playerComponent = CompMappers.playerComponentMapper.get(playerEntity)
                val playerPhysicsBody = CompMappers.physicsBodyMapper.get(playerEntity)
                val audioComponent = CompMappers.audioMapper.get(playerEntity)
                val bodyPosition = playerPhysicsBody.body!!.position
                world.rayCast(rayCastCallback, bodyPosition.x, bodyPosition.y, bodyPosition.x + playerComponent.range, bodyPosition.y)
                attackButton.touchable = Touchable.disabled
                audioComponent.play("Attack")
                cooldownTimer.start()
            }
        })
        attackButton.setPosition(Gdx.graphics.width - attackButton.width, attackButton.height)
        listOf(countDownLabel, scoreLabel, coinCounter, pauseButton, attackButton).forEach(uiStage::addActor)
    }

    //todo: make this abomination into a non-thread and non-coroutine function (timer class with time randomized between lower and upper bound)
    @OptIn(DelicateCoroutinesApi::class)
    private fun launchGenerationThread() {
        generationCoroutine = GlobalScope.launch {
            while (!generationCoroutine.isCancelled) {
                val generationDelayMillis = Random.nextInt(LOWER_GENERATION_INTERVAL, UPPER_GENERATION_INTERVAL).toLong()
                delay(generationDelayMillis)
                val buildingWidth = Random.nextDouble(MINIMUM_BUILDING_WIDTH, MAXIMUM_BUILDING_WIDTH).toFloat()
                val buildingHeight = Random.nextDouble(MINIMUM_BUILDING_HEIGHT, MAXIMUM_BUILDING_HEIGHT).toFloat()
                val x = Gdx.graphics.width.toFloat() + buildingWidth
                createBuilding(x + buildingWidth, 0f, buildingWidth, buildingHeight)
                createJumpingSurface(x + buildingWidth, buildingHeight + 1, buildingWidth)
                val coinSpawnProbability = Random.nextInt(0, 100)
                val spawnCoin = coinSpawnProbability in 40..60
                if (spawnCoin) {
                    val coinX = x + buildingWidth + coinTextures[CoinType.COIN_1]!!.width / 2
                    val coinY = buildingHeight + 75f
                    val amplitude = Random.nextDouble(MINIMUM_SINE_AMPLITUDE.toDouble(), MAXIMUM_SINE_AMPLITUDE.toDouble()).toFloat()
                    val frequency = Random.nextDouble(MINIMUM_SINE_FREQUENCY.toDouble(), MAXIMUM_SINE_FREQUENCY.toDouble()).toFloat()
                    createCoin(coinX, coinY, amplitude, frequency)
                }
            }
        }
    }

    private fun performGenerationStep() {

    }

    private fun stopGenerationThread() {
        generationCoroutine.cancel()
    }

    private fun pauseGame() {
        physicsSystem.setProcessing(false)
        velocitySystem.setProcessing(false)
        sineMovementSystem.setProcessing(false)
        animationSystem.setProcessing(false)
        stopGenerationThread()
    }

    private fun resumeGame() {
        physicsSystem.setProcessing(true)
        velocitySystem.setProcessing(true)
        sineMovementSystem.setProcessing(true)
        animationSystem.setProcessing(true)
        launchGenerationThread()
    }

    fun togglePause() {
        paused = !paused
    }

    private fun buildLevelStart() {
        createCoin(550f, 450f, 40f, 1f)
        createBuilding(0f, 0f, 150f, 150f)
        createJumpingSurface(0f, 151f, 150f)
        createBuilding(350f, 0f, 150f, 250f)
        createJumpingSurface(350f, 251f, 150f)
        createBuilding(650f, 0f, 225f, 400f)
        createJumpingSurface(650f, 401f, 225f)
        createBuilding(950f, 0f, 150f, 350f)
        createJumpingSurface(950f, 351f, 150f)
        createBuilding(1150f, 0f, 100f, 250f)
        createJumpingSurface(1150f, 251f, 100f)
    }

    private fun gameOver() {
        //here we will pass over the amount of player coins, their score, etc.
        val playerComponent = CompMappers.playerComponentMapper.get(playerEntity)
        val oldScreen = game.screen
        game.screen = GameOverScreen(game, spriteBatch, uiStage.batch,
                engine, world, assetManager, playerComponent.currentPoints, playerComponent.currentPlayerCoins)
        oldScreen.dispose()
    }

    private fun exitToMenu() {
        val dialog = object : Dialog("Are you sure you want to quit?", Skin(Gdx.files.internal("skins/uiskin.json"))) {
            override fun result(`object`: Any?) {
                (`object` as? Action)?.invoke()
            }
        }
        dialog.button("Yes", {
            val oldScreen = game.screen
            game.screen = game.mainMenuScreen.apply { updateInputProcessor() }
            oldScreen.dispose()
        }).button("No")
        dialog.show(uiStage)
    }

    private fun increaseGameSpeed(delta: Float) {
        if (paused) return
        currentGameSpeed += delta * gameSpeedUpMultiplier
        currentGameSpeed = MathUtils.clamp(currentGameSpeed, MAX_ENVIRONMENT_SCROLL_SPEED, BASIC_ENVIRONMENT_SCROLL_SPEED)
        velocitySystem.entities.forEach { e -> kotlin.run {
            val velocityComponent = CompMappers.environmentVelocityMapper.get(e)
            velocityComponent.velocity.set(currentGameSpeed, 0f)
        } }
    }

    private fun updateGameScore() {
        if (paused || !countPoints) return
        val playerComponent = CompMappers.playerComponentMapper.get(playerEntity)
        playerComponent.currentPoints++
        scoreLabel.setText("Score:\n${playerComponent.currentPoints}")
    }

    private fun checkForGameOver() {
        val playerTransform = CompMappers.transformMapper.get(playerEntity)
        if (playerTransform.position.y + playerTransform.size.y * playerTransform.scale.y < 0f) {
            gameOver()
        }
    }
}
