"""
API 测试脚本
"""
import requests
import json
import time

# 配置
AI_SERVICE_URL = "http://localhost:10020"
SPRING_BOOT_CALLBACK_URL = "http://localhost:10010/api/internal/tasks/callback"


def test_health():
    """测试健康检查"""
    print("\n" + "="*50)
    print("测试 1: 健康检查")
    print("="*50)
    
    response = requests.get(f"{AI_SERVICE_URL}/health")
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
    
    assert response.status_code == 200
    print("✅ 健康检查通过")


def test_generate():
    """测试生成思维导图"""
    print("\n" + "="*50)
    print("测试 2: 生成思维导图")
    print("="*50)
    
    data = {
        "taskId": "test-task-001",
        "videoUrl": "http://example.com/test-video.mp4",
        "callbackUrl": SPRING_BOOT_CALLBACK_URL
    }
    
    print(f"发送请求: {json.dumps(data, indent=2, ensure_ascii=False)}")
    
    response = requests.post(
        f"{AI_SERVICE_URL}/api/generate",
        json=data,
        headers={'Content-Type': 'application/json'}
    )
    
    print(f"\n状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
    
    assert response.status_code == 202
    print("\n✅ 任务已接收")
    
    # 等待异步处理完成
    print("\n等待任务处理（约3秒）...")
    time.sleep(4)
    print("✅ 任务应该已处理完成（请查看 Spring Boot 日志）")


def test_invalid_request():
    """测试无效请求"""
    print("\n" + "="*50)
    print("测试 3: 无效请求（缺少参数）")
    print("="*50)
    
    data = {
        "taskId": "test-task-002"
        # 缺少 videoUrl 和 callbackUrl
    }
    
    response = requests.post(
        f"{AI_SERVICE_URL}/api/generate",
        json=data
    )
    
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
    
    assert response.status_code == 400
    print("✅ 正确拒绝无效请求")


if __name__ == "__main__":
    print("\n" + "🚀 " + "="*46)
    print("   MindMOOC AI Service API 测试")
    print("="*50)
    
    try:
        test_health()
        test_generate()
        test_invalid_request()
        
        print("\n" + "="*50)
        print("✅ 所有测试通过！")
        print("="*50 + "\n")
        
    except requests.exceptions.ConnectionError:
        print("\n❌ 错误: 无法连接到 AI Service")
        print("请确保 Flask 服务已启动（python app.py）")
        
    except AssertionError as e:
        print(f"\n❌ 测试失败: {e}")
        
    except Exception as e:
        print(f"\n❌ 发生错误: {e}")

