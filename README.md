# DRL GAN

Reinforcement Learning (RL) boasts the ability of learning how to determine an agent's best action given a state. However, given its need of storing all the information required to describe the state, its usage is limited to simple problems. When the knowledge space grows Deep Reinforcement Learning (DRL) allows to handle the problem by learning from high dimensional sensory streams. However, when training aforesaid methods in fixed environments, they tend to overfit, leading to failure when applied to different environments. The issue has been studied and partially solved by involving procedural content generation to add variety to the training environment, but the DRL performances drop when the generated content is not similar enough to the final environment. In this paper we explore the possibility of using Generative Adversarial Networks (GANs) to generate an ideal training environment for DRL, generating content by extracting features of human-designed levels and using an agent in original human-designed levels.

The levels produced with GANs showed very good generalisation, producing very human-like levels with a large variety and combinations. Our DRL results show that overfitting to a problem can be positive when the test environment is known, as training on varied and more difficult levels did not produce better results on easy levels. We also proved the efficacy of progressive learning by only increasing the level of difficulty only when a certain performance has been reached, as that was the only method capable of achieving a learning process in human-like levels with complex mazes and patterns.

he project is based on these two repositories, that are used and edited.
Credits belong to:
- https://github.com/njustesen/a2c_gvgai
- https://github.com/TheHedgeify/DagstuhlGAN/

## Installing

- install pip
- sudo pip3 install torch
- sudo pip3 install torchvision
- sudo pip3 install mathplotlib
- sudo apt-get install python3-tk


## Creating Zelda Levels using GANs

Some already trained models are in rl-gan\pytorch
Some sets of levels are included as zip files in the root folders. Their description is in the paper relative to this project

### Train a model

- The model is trained using all the txt levels in rlgan\marioaiDagstuhl\zelda\levels, so create more or remove unnecessary levels from that folder according to the model you want to create

- Generate json files for levels in rlgan\marioaiDagstuhl\zelda\levels by running ZeldaReader.java

- Copy the exampleZelda.json from rlgan\marioaiDagstuhl\zelda to the folder rlgan\pytorch

- Delete or Backup the content of rl-gan\pytorch\samples

- Run main.py with the arguments:
'''python main.py --niter [AMOUNT] --zelda --cuda'''
where the AMOUNT is the number of iteriations of the training. We used 15000 for ours

- This will train the model and produce a .pth file in rl-gan\pytorch\samples

### Use the model to generate levels

- Copy the file (e.g. netG_epoch_15000_0_32.pth) from rl-gan\pytorch\samples to rl-gan\pytorch and rename it to "model.pth"

- Delete the content of rl-gan\GeneratedLevels (if any)

- Run CMAMarioSolverExport.java (from rl-gan\marioaiDagstuhl\src\reader). Adjust the parameters to run the desired number of iterations

- Exported levels will be in rl-gan\GeneratedLevels, divided by difficulty

## Training Zelda Agents using A2C
