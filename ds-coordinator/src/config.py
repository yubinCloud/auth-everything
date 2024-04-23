import os

from dynaconf import Dynaconf

# 从docker读取配置名（默认/线上环境），具体配置根据settings.yaml区分
os_env = "internal" if os.environ.get("ENV_FOR_DYNACONF") == "internal" else None

settings = Dynaconf(
    # envvar_prefix="DYNACONF",    # 去掉变量前缀
    envvar_prefix=False,    # 去掉变量前缀
    settings_files=['settings.yaml', '.secrets.yaml'],
    environments=True,    # 环境分层
    default_env="default",    # 默认环境
    env=os_env,
)

close_es = (os.environ.get("CLOSE_ES", "false")).upper()
if close_es == "TRUE":
    settings.__setattr__("CLOSE_ES", True)
else:
    settings.__setattr__("CLOSE_ES", False)


# `envvar_prefix` = export envvars with `export DYNACONF_FOO=bar`.
# `settings_files` = Load these files in the order.
