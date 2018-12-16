##!/bin/sh
#NUM_ENVS=8
#RUNS=30
#EXPERIMENT_NAME="zelda-ls-gan-progressive"
#EXPERIMENT_ID1="0"
#GAME="zelda"
#MODEL_STEPS=60000000
#
#for i in 0 1 2 3 4
#do
#    python3 eval.py --game $GAME --selector seq-human-$i --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#done
#
#
#python3 eval.py --game $GAME --selector seq-5 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-10 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-0 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-4 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-9 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-0-3 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#NUM_ENVS=8
#RUNS=30
#EXPERIMENT_NAME="zelda-ls-gan-progressive"
#EXPERIMENT_ID1="85f0b236-f74b-11e8-b761-7c76355264ee"
#GAME="zelda"
#MODEL_STEPS=60000000
#
#for i in 0 1 2 3 4
#do
#    python3 eval.py --game $GAME --selector seq-human-$i --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#done
#
#
#python3 eval.py --game $GAME --selector seq-5 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-10 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-0 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-4 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-9 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
#
#python3 eval.py --game $GAME --selector seq-gan-0-3 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS

NUM_ENVS=8
RUNS=30
EXPERIMENT_NAME="zelda-ls-gan-progressive-bell"
EXPERIMENT_ID1="8e919a20-fbb7-11e8-b771-7c76355264ee"
GAME="zelda"
MODEL_STEPS=60000000

for i in 0 1 2 3 4
do
    python3 eval.py --game $GAME --selector seq-human-$i --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS
done


python3 eval.py --game $GAME --selector seq-5 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS

python3 eval.py --game $GAME --selector seq-10 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS

python3 eval.py --game $GAME --selector seq-gan-0 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS

python3 eval.py --game $GAME --selector seq-gan-4 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS

python3 eval.py --game $GAME --selector seq-gan-9 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS

python3 eval.py --game $GAME --selector seq-gan-0-3 --num-envs $NUM_ENVS --runs $RUNS --experiment-name $EXPERIMENT_NAME --experiment-id $EXPERIMENT_ID1 --model-steps $MODEL_STEPS