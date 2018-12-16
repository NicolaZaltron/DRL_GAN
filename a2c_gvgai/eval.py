import tensorflow as tf
import argparse

from level_selector import *
from model import Model
from runner import Runner
from env import *

from baselines.a2c.utils import make_path
from baselines.a2c.policies import CnnPolicy

from baselines.common import set_global_seeds
from baselines.ppo2.policies import CnnPolicy, LstmPolicy, LnLstmPolicy


def eval(model, env, nsteps=5, runs=100, render=False, record_name=None, level_selector=None):

    runner = Runner(env, model, nsteps=nsteps, gamma=0, render=render,record_name=record_name)

    while len(runner.final_rewards) < runs:
        obs, states, rewards, masks, actions, values = runner.run()    

    scores = runner.final_rewards[:runs]
    mean_score = np.mean(scores)
    std_score = np.std(scores)

    return scores

def test_on(game, level, selector, experiment_name, experiment_id, policy, num_envs=1, seed=0, runs=100, render=False, record_path=None, save_results=True, model_steps=-1):

    # Environment name
    env_id = "gvgai-" + game + "-lvl" + str(level) + "-v0"

    # Test name
    test_name = game
    if selector is not None:
        test_name += "-ls-" + selector
    else:
        test_name += "-lvl-" + str(level)

    print("Test name: " + test_name)
    print('Training name: ' + experiment_name)
    print("Training id: " + experiment_id)

    # Folders
    score_path = './results/' + experiment_name + '/eval/' + test_name + '/scores/'
    level_path = './results/' + experiment_name + '/eval/' + test_name + '/levels/'
    make_path(level_path)
    make_path(score_path)

    # Create file and override if necessary
    score_file = score_path + test_name + "_" + experiment_id + ".dat"
    with open(score_file, 'w+') as myfile:
        myfile.write('')

    # Level selector
    level_selector = LevelSelector.get_selector(selector, game, level_path, max=runs)

    env = make_gvgai_env(env_id, num_envs, seed, level_selector=level_selector)

    # Main plots per experiment
    mean_scores = []
    std_scores = []
    model_folder = './results/' + experiment_name + '/models/' + experiment_id + "/"

    # Find number of steps for last model
    if model_steps < 0:
        for model_meta_name in glob.iglob(model_folder + '*.meta'):
            print(model_meta_name)
            s = int(model_meta_name.split('.meta')[0].split('/')[-1].split("-")[1])
            print(s)
            if s > model_steps:
                model_steps = s

    print(model_steps)

    if policy == 'cnn':
        policy_fn = CnnPolicy
    elif policy == 'lstm':
        policy_fn = LstmPolicy
    elif policy == 'lnlstm':
        policy_fn = LnLstmPolicy

    tf.reset_default_graph()

    ob_space = env.observation_space
    ac_space = env.action_space
    print("creating model")
    model = Model(policy=policy_fn, ob_space=ob_space, ac_space=ac_space, nenvs=num_envs, nsteps=5)

    print("loading model")
    try:
        model.load(model_folder, model_steps)
    except Exception as e:
        print(e)
        env.close()
        return

    if record_path is not None:
    	name = "{}/{}_{}_steps_{}m".format(record_path, experiment_name,policy,model_steps/1000000)
    else:
        name = None

    print("evaluate")
    scores = eval(model, env, runs=runs, render=render, record_name=name, level_selector=level_selector)

    mean_score = np.mean(scores)
    std_score = np.std(scores)
    print("Testing on=" + test_name)
    print("Trained on=" + experiment_name)
    print("Model id=" + experiment_id)
    print("Steps trained=" + str(model_steps))
    print("Runs=" + str(runs))
    print("Mean score=" + str(mean_score))
    print("Std. dev.=" + str(std_score))
    print("All scores=" + str(scores))


    if save_results:
        print("saving results to " + score_file)
        # Save results
        with open(score_file, "a") as myfile:
            line = "Testing on=" + test_name + "\n"
            line += "Trained on=" + experiment_name + "\n"
            line += "Id=" + experiment_id + "\n"
            line += "Steps trained=" + str(model_steps) + "\n"
            line += "Runs=" + str(runs) + "\n"
            line += "Mean score=" + str(mean_score) + "\n"
            line += "Std. dev.=" + str(std_score) + "\n"
            line += "All scores=" + str(scores) + "\n"
            line += "\n"
            myfile.write(line)

    env.close()


def main():
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument('--policy', help='Policy architecture', choices=['cnn', 'lstm', 'lnlstm'], default='cnn')
    parser.add_argument('--runs', help='Number of runs for each model', type=int, default=100)
    parser.add_argument('--num-envs', help='Number of environments/workers to run in parallel', type=int, default=10)
    parser.add_argument('--game', help='Game name (default=zelda)', default='zelda')
    parser.add_argument('--seed', help='RNG seed', type=int, default=0)
    parser.add_argument('--experiment-name', help='Name of the experiment to evaluate, e.g. zelda-ls-pcg-random (default=None -> all)', default=None)
    parser.add_argument('--experiment-id', help='Id of the experiment to evaluate')
    parser.add_argument('--model-steps', help='If specified, it loads a model trained on this amount of steps. If not, it loads the model trained on most steps.', type=int, default=-1)
    parser.add_argument('--level', help='Level (integer) to train on', type=int, default=0)
    parser.add_argument('--selector',
                        help='Level selector to use in training - will ignore the level argument if set (default: None)',
                        choices=[None] + LevelSelector.available, default=None)
    parser.add_argument('--record_path', help='Provide path to save video of agent play', default=None)
    parser.add_argument('--render', action='store_true',
                        help='Render screen (default: False)')
    parser.add_argument('--no-save', action='store_true',
                        help='Disable result saving (default: False)')

    args = parser.parse_args()

    test_on(args.game,
            args.level,
            args.selector,
            experiment_name=args.experiment_name,
            experiment_id=args.experiment_id,
            policy=args.policy,
            runs=args.runs,
            seed=args.seed,
            num_envs=args.num_envs,
            render=args.render,
            record_path=args.record_path,
            save_results=not args.no_save,
            model_steps=args.model_steps)


if __name__ == '__main__':
    main()
