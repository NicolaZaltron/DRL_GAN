import level_generator as LG
import os


lg0 = LG.ParamGenerator(os.path.realpath('.')+'/pcg_levels/0','zelda',13,9)

lg5 = LG.ParamGenerator(os.path.realpath('.')+'/pcg_levels/5','zelda',13,9)

for i in range(1000):
    if i%20==0:
        print(str(i))
    lg0.generate([0], difficulty=True)
    lg5.generate([0.5], difficulty=True)