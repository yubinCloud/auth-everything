import os

from dynaconf import Dynaconf

# 从docker读取配置名（默认/线上环境），具体配置根据settings.yaml区分
docker_global_env = os.environ.get("ENV_FOR_DYNACONF", "default")

settings = Dynaconf(
    # envvar_prefix="DYNACONF",                               # 去掉变量前缀。
    envvar_prefix=False,                                      # 去掉变量前缀
    settings_files=['settings.yaml', '.secrets.yaml'],
    environments=True,                                        # 环境分层
    default_env="default",                                    # 默认环境
    env=docker_global_env,
)

