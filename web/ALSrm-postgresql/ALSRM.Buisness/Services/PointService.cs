using System;
using System.Linq;
using System.Threading.Tasks;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using ALSRM.Domain.Contracts.Repositories;
using ALRSM.Resources.Resources;

namespace ALSRM.Buisness.Services
{
    public class PointService : IPointService
    {
        IPointRepository _repository;

        public PointService(IPointRepository repository)

        {
            this._repository = repository;
        }

        public IQueryable<Point> Get()
        {
            return _repository.Get();
        }

        public IQueryable<Point> Get(int examId, int stepNum)
        {
            return _repository.Get(examId, stepNum);
        }

        public IQueryable<Point> Get(int examId, int stepNum, decimal x)
        {
            return _repository.Get(examId, stepNum, x);
        }

        public async Task<int> ChangeInformationAsync(int examId, int stepNum, decimal x, decimal y)
        {
            Point point = await FindAsync(examId, stepNum, x);

            if (point == null)
                throw new Exception(Errors.InvalidPoint);

            point.ChangeY(y);
            point.Validate();

            return await _repository.UpdateAsync(point);
        }

        public async Task<Point> FindAsync(int examId, int stepNum, decimal x)
        {
            return await _repository.FindAsync(examId, stepNum, x);
        }

        public async Task<Point> RegisterAsync(int examId, int stepNum, decimal x, decimal y)
        {
            Point point = new Point(examId, stepNum, x, y);
            return await _repository.CreateAsync(point);
        }

        public async Task<int> SaveRepositoryAsync()
        {
            return await _repository.SaveRepositoryAsync();
        }

        public void Dispose()
        {
            _repository.Dispose();
        }
    }
}
